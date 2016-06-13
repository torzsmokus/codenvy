package com.codenvy.api.account;

import com.codenvy.api.account.impl.AccountImpl;

/**
 * @author Sergii Leschenko
 */
public interface AccountDao {

    void create(AccountImpl id);
}
