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

import org.eclipse.che.api.core.NotFoundException;

import javax.inject.Inject;

/**
 * @author Sergii Leschenko
 */
public class AccountManager {
    private final AccountDao accountDao;

    @Inject
    public AccountManager(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Returns account by specified id
     *
     * @param accountId
     *         id of account
     */
    public Account getById(String accountId) throws NotFoundException {
        return accountDao.getById(accountId);
    }

    /**
     * Returns account by specified name
     *
     * @param accountName
     *         name of account
     */
    public Account getByName(String accountName) throws NotFoundException {
        return accountDao.getByName(accountName);
    }

}
