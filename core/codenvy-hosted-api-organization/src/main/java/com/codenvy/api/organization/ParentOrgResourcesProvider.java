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
package com.codenvy.api.organization;

import com.codenvy.api.account.Account;
import com.codenvy.api.account.Account.Type;
import com.codenvy.api.account.AccountManager;
import com.codenvy.api.organization.model.Organization;
import com.codenvy.api.resources.ResourcesManager;
import com.codenvy.api.resources.ResourcesProvider;
import com.codenvy.api.resources.model.Resource;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Provides resources from parent organization for account with type {@link Type#ORGANIZATIONAL}
 *
 * @author Sergii Leschenko
 */
public class ParentOrgResourcesProvider implements ResourcesProvider {
    private final ResourcesManager    resourceManager;
    private final AccountManager      accountManager;
    private final OrganizationManager organizationManager;

    @Inject
    public ParentOrgResourcesProvider(ResourcesManager resourceManager,
                                      AccountManager accountManager,
                                      OrganizationManager organizationManager) {
        this.resourceManager = resourceManager;
        this.accountManager = accountManager;
        this.organizationManager = organizationManager;
    }

    @Override
    public List<Resource> getAvailableResources(String accountId) {
        final Account account = accountManager.getById(accountId);
        if (Type.ORGANIZATIONAL.equals(account.getType())) {
            final Organization organization = organizationManager.getById(account.getName());
            final Organization parentOrg = organizationManager.getById(organization.getParent());
            final Account parentAccount = accountManager.getByName(parentOrg.getName());
            return resourceManager.getAvailableResources(parentAccount.getId());
        } else {
            return Collections.emptyList();
        }
    }
}
