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
package com.codenvy.api.dao.ldap;

import com.codenvy.api.event.user.RemoveUserEvent;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.model.user.User;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * LDAP based implementation of {@code UserDao}.
 *
 * @author andrew00x
 */
@Singleton
public class LdapUserDao implements UserDao {
    private static final Logger LOG = LoggerFactory.getLogger(LdapUserDao.class);

    protected final String                    userObjectClassFilter;
    protected final String                    containerDn;
    protected final String                    userDn;
    protected final String                    oldUserDn;
    protected final EventService              eventService;
    protected final UserAttributesMapper      mapper;
    protected final InitialLdapContextFactory contextFactory;


    /**
     * Creates new instance of {@code LdapUserDao}.
     *
     * @param userContainerDn
     *         full name of root object for user records, e.g. {@code ou=People,dc=codenvy,dc=com}
     * @param userAttributesMapper
     *         UserAttributesMapper
     */
    @Inject
    public LdapUserDao(InitialLdapContextFactory contextFactory,
                       @Named("user.ldap.user_container_dn") String userContainerDn,
                       @Named("user.ldap.user_dn") String userDn,
                       @Named("user.ldap.old_user_dn") String oldUserDn,
                       UserAttributesMapper userAttributesMapper,
                       EventService eventService) {
        this.contextFactory = contextFactory;
        this.containerDn = userContainerDn;
        this.userDn = userDn;
        this.oldUserDn = oldUserDn;
        this.mapper = userAttributesMapper;
        this.eventService = eventService;
        final StringBuilder sb = new StringBuilder();
        for (String objectClass : userAttributesMapper.userObjectClasses) {
            sb.append("(objectClass=");
            sb.append(objectClass);
            sb.append(')');
        }
        this.userObjectClassFilter = sb.toString();
    }

    @Override
    public String authenticate(String alias, String password) throws UnauthorizedException, ServerException {
        if (isNullOrEmpty(alias) || isNullOrEmpty(password)) {
            throw new UnauthorizedException(
                    String.format("Can't perform authentication for user '%s'. Username or password is empty", alias));
        }
        UserImpl user;
        try {
            user = doGetByAttribute(mapper.userEmailAttr, alias);
            if (user == null) {
                user = doGetByAttribute(mapper.userNameAttr, alias);
            }
            if (user == null) {
                user = doGetByAttribute(mapper.userAliasesAttr, alias);
            }
            if (user == null) {
                throw new UnauthorizedException(format("Authentication failed for user '%s'", alias));
            }
            InitialLdapContext authContext = null;
            final String principal = formatDn(userDn, user.getId());
            try {
                authContext = contextFactory.createContext(principal, password);
            } catch (AuthenticationException e) {
                //if first time authentication failed, try to rename user entity
                doGetById(user.getId());
                //retry authentication
                try {
                    authContext = contextFactory.createContext(principal, password);
                } catch (AuthenticationException e2) {
                    throw new UnauthorizedException(format("Authentication failed for user '%s'", principal));
                }
            } finally {
                close(authContext);
            }
        } catch (NamingException e) {
            throw new ServerException(format("Error during authentication of user '%s'", alias), e);
        }
        return user.getId();
    }

    @Override
    public void create(UserImpl user) throws ConflictException, ServerException {
        InitialLdapContext context = null;
        DirContext newContext = null;
        try {
            final String email = user.getEmail();
            if (email != null && doGetByAttribute(mapper.userEmailAttr, email) != null) {
                throw new ConflictException(format("User with email '%s' already exists", email));
            }
            for (String alias : user.getAliases()) {
                if (doGetByAttribute(mapper.userAliasesAttr, alias) != null) {
                    throw new ConflictException(format("User with alias '%s' already exists", alias));
                }
            }
            //TODO consider null value for user name
            if (user.getName() != null && doGetByAttribute(mapper.userNameAttr, user.getName()) != null) {
                throw new ConflictException(format("User with name '%s' already exists", user.getName()));
            }
            context = contextFactory.createContext();
            newContext = context.createSubcontext(formatDn(userDn, user.getId()), mapper.toAttributes(user));
        } catch (NameAlreadyBoundException e) {
            throw new ConflictException(format("Unable create new user '%s'. User already exists", user.getId()));
        } catch (NamingException e) {
            throw new ServerException(format("Unable create new user '%s'", user.getEmail()), e);
        } finally {
            close(newContext);
            close(context);
        }
    }

    @Override
    public void update(UserImpl update) throws NotFoundException, ServerException, ConflictException {
        final String id = update.getId();
        try {
            final UserImpl target = doGetById(id);

            //check that user exists
            if (target == null) {
                throw new NotFoundException("User " + id + " was not found");
            }

            //check that new aliases are unique
            final Set<String> newAliases = new HashSet<>(update.getAliases());
            newAliases.removeAll(target.getAliases());
            for (String alias : newAliases) {
                if (doGetByAttribute(mapper.userAliasesAttr, alias) != null) {
                    throw new ConflictException(format("Unable update user '%s', alias '%s' is already in use", id, alias));
                }
            }

            //check that new name is unique
            if (!target.getName().equals(update.getName())
                && doGetByAttribute(mapper.userNameAttr, update.getName()) != null) {
                throw new ConflictException(format("Unable update user '%s', name '%s' already in use", id, update.getName()));
            }

            //check that new email is unique
            if (!target.getEmail().equals(update.getEmail())
                && doGetByAttribute(mapper.userEmailAttr, update.getEmail()) != null) {
                throw new ConflictException(format("Unable update user '%s', email '%s' already in use", id, update.getName()));
            }

            InitialLdapContext context = null;
            try {
                final ModificationItem[] mods = mapper.createModifications(target, update);
                if (mods.length > 0) {
                    context = contextFactory.createContext();
                    context.modifyAttributes(formatDn(userDn, id), mods);
                }
            } catch (NamingException e) {
                throw new ServerException(format("Unable update (user) '%s'", update.getEmail()), e);
            } finally {
                close(context);
            }
        } catch (NamingException e) {
            throw new ServerException(format("Unable update user '%s'", update.getEmail()), e);
        }
    }

    @Override
    public void remove(String id) throws ServerException, ConflictException {
        InitialLdapContext context = null;
        try {
            context = contextFactory.createContext();
            context.destroySubcontext(formatDn(userDn, id));
            eventService.publish(new RemoveUserEvent(id));
        } catch (NameNotFoundException e) {
            // according to the interface contract it is okay if user doesn't exist
        } catch (NamingException e) {
            throw new ServerException(format("Unable remove user '%s'", id), e);
        } finally {
            close(context);
        }
    }

    @Override
    public UserImpl getByAlias(String alias) throws NotFoundException, ServerException {
        try {
            final User user = doGetByAttribute(mapper.userAliasesAttr, alias);
            if (user == null) {
                throw new NotFoundException("User not found " + alias);
            }
            return new UserImpl(user);
        } catch (NamingException e) {
            throw new ServerException(format("Unable get user '%s'", alias), e);
        }
    }

    @Override
    public UserImpl getById(String id) throws NotFoundException, ServerException {
        try {
            final User user = doGetById(id);
            if (user == null) {
                throw new NotFoundException("User " + id + " was not found ");
            }
            return new UserImpl(user);
        } catch (NamingException e) {
            throw new ServerException(format("Unable get user '%s'", id), e);
        }
    }

    @Override
    public UserImpl getByName(String name) throws NotFoundException, ServerException {
        try {
            final User user = doGetByAttribute(mapper.userNameAttr, name);
            if (user == null) {
                throw new NotFoundException("User " + name + " was not found ");
            }
            return new UserImpl(user);
        } catch (NamingException e) {
            throw new ServerException(format("Unable get user '%s'", name), e);
        }
    }

    @Override
    public UserImpl getByEmail(String email) throws NotFoundException, ServerException {
        try {
            final UserImpl user = doGetByAttribute(mapper.userEmailAttr, email);
            if (user == null) {
                throw new NotFoundException("User " + email + " was not found ");
            }
            return new UserImpl(user);
        } catch (NamingException e) {
            throw new ServerException(format("Unable get user '%s'", email), e);
        }
    }

    private UserImpl doGetByAttribute(String name, String value) throws NamingException {
        UserImpl user = null;
        InitialLdapContext context = null;
        try {
            context = contextFactory.createContext();
            final Attributes attributes = getUserAttributesByFilter(context, createFilter(name, value));
            if (attributes != null) {
                user = mapper.fromAttributes(attributes);
            }
        } finally {
            close(context);
        }
        return user;
    }

    private UserImpl doGetById(String id) throws NamingException {
        UserImpl user = null;
        InitialLdapContext context = null;
        try {
            context = contextFactory.createContext();
            final Attributes attributes = getUserAttributesById(context, id);
            if (attributes != null) {
                user = mapper.fromAttributes(attributes);
            }
        } finally {
            close(context);
        }
        return user;
    }

    private Attributes getUserAttributesById(InitialLdapContext context, String id) throws NamingException {
        try {
            //try to find user using dn
            return context.getAttributes(formatDn(userDn, id));
        } catch (NameNotFoundException nfEx) {
            //if not found -> try to find user using old dn
            try {
                final Attributes attributes = context.getAttributes(formatDn(oldUserDn, id));

                //if attributes were found then rename current entity
                final String fromDnVal = attributes.get(oldUserDn).get().toString();
                final String toDnVal = attributes.get(userDn).get().toString();
                context.rename(formatDn(oldUserDn, fromDnVal), formatDn(userDn, toDnVal));

                return attributes;
            } catch (NameNotFoundException nfEx2) {
                return null;
            }
        }
    }

    private String formatDn(String userDn, String id) {
        return userDn + '=' + id + ',' + containerDn;
    }

    private String createFilter(String attribute, String value) {
        return "(&(" + attribute + '=' + value + ")(" + userObjectClassFilter + "))";
    }

    private Attributes getUserAttributesByFilter(InitialLdapContext context, String filter) throws NamingException {
        final SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> enumeration = null;
        try {
            enumeration = context.search(containerDn, filter, controls);
            if (enumeration.hasMore()) {
                return enumeration.nextElement().getAttributes();
            }
            return null;
        } finally {
            close(enumeration);
        }
    }

    protected void close(Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void close(NamingEnumeration<SearchResult> enumeration) {
        if (enumeration != null) {
            try {
                enumeration.close();
            } catch (NamingException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
    }
}
