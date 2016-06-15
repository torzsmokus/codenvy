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
package com.codenvy.api;

import com.codenvy.api.account.OrganizationAccountProvider;
import com.codenvy.api.account.UserAccountProvider;
import com.codenvy.api.organization.OrganizationDao;
import com.codenvy.api.organization.OrganizationService;
import com.google.inject.AbstractModule;

import org.eclipse.che.api.user.server.dao.UserDao;

import static com.google.inject.matcher.Matchers.subclassesOf;
import static org.eclipse.che.inject.Matchers.names;


/**
 * @author Sergii Leschenko
 */
public class OrganizationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(OrganizationService.class);

        UserAccountProvider userAccountProvider = new UserAccountProvider();
        requestInjection(userAccountProvider);
        bindInterceptor(subclassesOf(UserDao.class), names("create"), userAccountProvider);

        OrganizationAccountProvider organizationAccountProvider = new OrganizationAccountProvider();
        requestInjection(organizationAccountProvider);
        bindInterceptor(subclassesOf(OrganizationDao.class), names("create"), organizationAccountProvider);
    }
}
