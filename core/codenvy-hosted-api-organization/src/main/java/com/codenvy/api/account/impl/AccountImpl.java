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
package com.codenvy.api.account.impl;

import com.codenvy.api.account.Account;
import com.google.common.base.Objects;

/**
 * @author Sergii Leschenko
 */
public class AccountImpl implements Account {
    private final String id;
    private       String name;
    private final Type   type;

    public AccountImpl(String id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountImpl)) return false;
        AccountImpl account = (AccountImpl)o;
        return Objects.equal(id, account.id) &&
               Objects.equal(name, account.name) &&
               Objects.equal(type, account.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, type);
    }

    @Override
    public String toString() {
        return "AccountImpl{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", type=" + type +
               '}';
    }
}
