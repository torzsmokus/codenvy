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
package com.codenvy.api.account;

import com.codenvy.api.account.impl.AccountImpl;
import com.codenvy.api.organization.OrganizationDao;
import com.codenvy.api.organization.model.impl.OrganizationImpl;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.dao.User;
import org.eclipse.che.api.user.server.dao.UserDao;

import javax.inject.Inject;

/**
 * @author Sergii Leschenko
 */
public class AccountManager {
    private final OrganizationDao organizationDao;
    private final UserDao         userDao;

    @Inject
    public AccountManager(OrganizationDao organizationDao, UserDao userDao) {
        this.organizationDao = organizationDao;
        this.userDao = userDao;
    }

    /**
     * Returns account by specified name
     *
     * @param accountName
     *         name of account
     */
    public Account getByName(String accountName) throws NotFoundException, ServerException {
        try {
            final User user = userDao.getByName(accountName);
            return new AccountImpl(user.getName(), Account.Type.PERSONAL);
        } catch (NotFoundException userNotFound) {
            try {
                final OrganizationImpl organization = organizationDao.getByName(accountName);
                return new AccountImpl(organization.getName(), Account.Type.ORGANIZATIONAL);
            } catch (NotFoundException organizationNotFound) {
                throw new NotFoundException(String.format("Account with name '%s' was not found", accountName));
            }
        }
    }
}
