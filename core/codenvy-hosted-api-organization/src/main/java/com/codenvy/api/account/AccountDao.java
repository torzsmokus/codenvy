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
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author Sergii Leschenko
 */
public class AccountDao {
    private final HashMap<String, AccountImpl> accounts = new HashMap<>();
    private final LocalStorage accountStorage;

    @Inject
    public AccountDao(LocalStorageFactory storageFactory) throws IOException {
        this.accountStorage = storageFactory.create("account.json");
    }

    @PostConstruct
    public void load() {
        accounts.putAll(accountStorage.loadMap(new TypeToken<Map<String, AccountImpl>>() {}));
    }

    @PreDestroy
    public void save() throws IOException {
        accountStorage.store(accounts);
    }

    public void create(AccountImpl account) throws ConflictException {
        final Optional<AccountImpl> optAccount = accounts.values()
                                                         .stream()
                                                         .filter(existedAccount -> existedAccount.getName().equals(account.getName())
                                                                                   || existedAccount.getId().equals(account.getId()))
                                                         .findAny();
        if (optAccount.isPresent()) {
            throw new ConflictException("Account with given id or name already exists");
        }

        accounts.put(account.getId(), account);
    }

    public Account getById(String accountId) throws NotFoundException {
        final AccountImpl account = accounts.get(accountId);
        if (account == null) {
            throw new NotFoundException(format("Account with id '%s' was not found", accountId));
        }
        return account;
    }

    public Account getByName(String accountName) throws NotFoundException {
        final Optional<AccountImpl> optAccount = accounts.values()
                                                         .stream()
                                                         .filter(account -> account.getName().equals(accountName))
                                                         .findAny();
        if (!optAccount.isPresent()) {
            throw new NotFoundException(format("Account with name '%s' was not found", accountName));
        }
        return optAccount.get();
    }
}
