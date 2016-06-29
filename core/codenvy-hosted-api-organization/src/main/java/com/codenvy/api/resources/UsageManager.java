/*
 *  [2012] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.resources;

import com.codenvy.api.account.Account;
import com.codenvy.api.account.AccountManager;
import com.codenvy.api.organization.OrganizationManager;
import com.codenvy.api.organization.model.Limit;
import com.codenvy.api.organization.model.Organization;
import com.codenvy.api.resources.model.Resource;
import com.codenvy.api.resources.model.Session;
import com.codenvy.api.resources.model.impl.ResourceImpl;
import com.codenvy.api.resources.model.impl.SessionImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.workspace.server.WorkspaceManager;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.commons.lang.NameGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.codenvy.api.account.Account.Type.ORGANIZATIONAL;
import static com.codenvy.api.resources.ResourceHelper.groupResources;
import static java.lang.String.format;

/**
 * @author gazarenkov
 * @author Sergii Leschenko
 */
@Singleton
public class UsageManager {
    private final WorkspaceManager    workspaceManager;
    private final SessionDao          sessionDao;
    private final AccountManager      accountManager;
    private final OrganizationManager organizationManager;

    /** Stores own (license+promotions) resources of account */
    private final LoadingCache<String, List<ResourceImpl>> ownResourcesCache;
    /** Stores accessible resources (own + parent org) for each organization */
    private final LoadingCache<String, List<ResourceImpl>> accessibleResourcesCache;
    /** Stores active sessions for each account */
    private final Multimap<String, SessionImpl>            accountToActiveSessions;
    /** Stores active session in workspaces */
    private final Map<String, SessionImpl>                 workspaceToActiveSession;

    /** Stores used RAM by each account */
    private final Multimap<String, ResourceImpl> accountToUsedResources;

    //    /** Stores root organization of organization. Uses for locking organization tree */
//    private final Multimap<String, String>                 organizationToRootParents;
    @Inject
    public UsageManager(WorkspaceManager workspaceManager,
                        SessionDao sessionDao,
                        AccountManager accountManager,
                        OrganizationManager organizationManager,
                        Set<ResourcesProvider> resourcesProviders) {
        this.ownResourcesCache = CacheBuilder.newBuilder()
                                             .maximumSize(1000)
                                             .expireAfterWrite(10, TimeUnit.MINUTES)
                                             .build(new CacheLoader<String, List<ResourceImpl>>() {
                                                 @Override
                                                 public List<ResourceImpl> load(String account) throws Exception {
                                                     List<ResourceImpl> allResources = new ArrayList<>();
                                                     for (ResourcesProvider resourcesProvider : resourcesProviders) {
                                                         allResources.addAll(resourcesProvider.getAvailableResources(account));
                                                     }
                                                     return groupResources(allResources);
                                                 }
                                             });

        this.accessibleResourcesCache =
                CacheBuilder.newBuilder()
                            .maximumSize(1000)
                            .expireAfterWrite(10, TimeUnit.MINUTES)
                            .build(new CacheLoader<String, List<ResourceImpl>>() {
                                @Override
                                public List<ResourceImpl> load(String organizationName) throws Exception {
                                    //add to parent resources
                                    Organization organization = organizationManager.getByName(organizationName);

                                    if (organization.getParent() == null) {
                                        //root organization doesn't have parent resources
                                        return ownResourcesCache.get(organizationName);
                                    }

                                    final Limit parentLimit = organizationManager.getParentLimit(organization.getId());

                                    return new ArrayList<>(filterResources(ownResourcesCache.get(organization.getParent()), parentLimit));
                                }
                            });

        this.workspaceManager = workspaceManager;
        this.sessionDao = sessionDao;
        this.accountManager = accountManager;
        this.organizationManager = organizationManager;
        this.accountToActiveSessions = HashMultimap.create();
        this.workspaceToActiveSession = new HashMap<>();
        this.accountToUsedResources = HashMultimap.create();
//        this.organizationToRootParents = HashMultimap.create();
    }

    /**
     * called by resource consumer (machine service)
     *
     * @param userId
     *         id of user that init start of resources consuming
     * @param workspaceId
     *         id of workspace that start consume resources
     */
    public Session start(String userId, String workspaceId, String envName) throws ConflictException,
                                                                                   ServerException,
                                                                                   NotFoundException {
        final WorkspaceImpl workspace = workspaceManager.getWorkspace(workspaceId);
        final String accountName = workspace.getNamespace();
        Account account = accountManager.getByName(accountName);
        Account parentAccount = account;
        if (ORGANIZATIONAL.equals(account.getType())) {
            //TODO Add synchronization by root accounts
            parentAccount = getRootParent(parentAccount.getName());
        }

        final Resource ramResource = getRamResource(getAvailableResources(account.getName()));
        long workspaceRam = getWorkspaceRam(workspace, envName);
        if (ramResource.getAmount() < workspaceRam) {
            throw new ConflictException(format("Account has '%s'mb of available RAM but wants use '%s'mb",
                                               ramResource.getAmount(), workspaceRam));
        }

        final List<ResourceImpl> resourcesToUse = Collections.singletonList(new ResourceImpl("ram", workspaceRam));
        final SessionImpl session = new SessionImpl(NameGenerator.generate("session", 16),
                                                    userId,
                                                    accountName,
                                                    workspaceId,
                                                    resourcesToUse,
                                                    System.currentTimeMillis(),
                                                    System.currentTimeMillis(),
                                                    "start");
        final List<ResourceImpl> usedResources = new ArrayList<>(accountToUsedResources.get(accountName));
        usedResources.add(new ResourceImpl("ram", workspaceRam));
        accountToUsedResources.replaceValues(accountName, ResourceHelper.groupResources(usedResources));
        workspaceToActiveSession.put(workspaceId, session);
        sessionDao.store(session);
        return session;

    }

    private ResourceImpl getRamResource(List<ResourceImpl> resources) throws ConflictException {
        final Optional<ResourceImpl> optRam = resources.stream()
                                                       .filter(resource -> resource.getType().equals(
                                                               "RAM"))
                                                       .findAny();
        if (!optRam.isPresent()) {
            throw new ConflictException("Account don't have RAM to use");
        }

        return optRam.get();
    }

    /**
     * Returns active sessions for given account
     *
     * @param account
     *         id of account
     */
    public List<Session> getActiveSessions(String account) {
        return new ArrayList<>(accountToActiveSessions.get(account));
    }

    /**
     * Should be called to notify that session in workspace is still active
     *
     * @param workspace
     *         id of workspace
     */
    public void tick(String workspace) throws NotFoundException {
        sessionDao.updateStop(workspaceToActiveSession.get(workspace).getId(), System.currentTimeMillis(), "tick");
    }

    /**
     * Should be called to notify that session in workspace is still stopped
     *
     * @param workspace
     *         id of workspace
     */
    public void stop(String workspace) throws NotFoundException {
        final SessionImpl session = workspaceToActiveSession.get(workspace);
        sessionDao.updateStop(session.getId(), System.currentTimeMillis(), "stop");
        workspaceToActiveSession.remove(workspace);

        final String account = session.getAccount();
        accountToActiveSessions.remove(account, session);
        final List<ResourceImpl> resources = new ArrayList<>(accountToUsedResources.get(account));
        accountToUsedResources.replaceValues(account, ResourceHelper.reduceUsedResources(resources, session.getResources()));
    }

    /**
     * Returns list of resources which are available for given account
     *
     * @param accountName
     *         name of account
     */
    public List<ResourceImpl> getAvailableResources(String accountName) throws NotFoundException, ServerException {
        final Account account = accountManager.getByName(accountName);
        List<ResourceImpl> accessibleResources;
        if (Account.Type.ORGANIZATIONAL.equals(account.getType())) {
            try {
                accessibleResources = accessibleResourcesCache.get(accountName);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            accessibleResources = safeGetOwnResources(account.getName());
        }
        return ResourceHelper.reduceUsedResources(accessibleResources, getUsedResources(accountName));
    }

    private List<ResourceImpl> safeGetOwnResources(String accountName) throws NotFoundException, ServerException {
        try {
            return ownResourcesCache.get(accountName);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NotFoundException) {
                throw ((NotFoundException)e.getCause());
            } else if (e.getCause() instanceof ServerException) {
                throw ((ServerException)e.getCause());
            }
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    private List<ResourceImpl> filterResources(List<ResourceImpl> parentAvailableResources, Limit parentLimit) {
        //TODO Fix it
        return parentAvailableResources;
    }

    /**
     * Returns list of resources which are used by given account
     *
     * @param account
     *         id of account
     */
    public List<ResourceImpl> getUsedResources(String account) {
        throw new UnsupportedOperationException();
    }

    private Account getRootParent(String childId) throws NotFoundException, ServerException {
        Organization parentOrg = organizationManager.getById(childId);
        while (parentOrg.getParent() != null) {
            parentOrg = organizationManager.getById(parentOrg.getId());
        }
        return accountManager.getByName(parentOrg.getName());
    }

    private long getWorkspaceRam(WorkspaceImpl workspace, String envName) throws NotFoundException {
        final Optional<EnvironmentImpl> optEnvironment = workspace.getConfig().getEnvironment(envName);

        if (!optEnvironment.isPresent()) {
            throw new NotFoundException(format("Workspace '%s:%s' doesn't contain environment '%s'",
                                               workspace.getNamespace(),
                                               workspace.getConfig().getName(),
                                               envName));
        }

        final EnvironmentImpl environment = optEnvironment.get();
        return (long)environment.getMachineConfigs()
                                .stream()
                                .filter(machineCfg -> machineCfg.getLimits() != null)
                                .mapToInt(machineCfg -> machineCfg.getLimits().getRam())
                                .sum();
    }
}
