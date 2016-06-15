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
package com.codenvy.api.organization.model.impl;

import com.codenvy.api.organization.model.Member;

import java.util.List;
import java.util.Objects;

/**
 * @author Sergii Leschenko
 */
public class MemberImpl implements Member {
    private final String       user;
    private final String       organization;
    private       List<String> actions;

    public MemberImpl(String user, String organization, List<String> actions) {
        this.user = user;
        this.organization = organization;
        this.actions = actions;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getOrganization() {
        return organization;
    }

    @Override
    public List<String> getPermittedActions() {
        return actions;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MemberImpl)) return false;
        final MemberImpl other = (MemberImpl)obj;
        return Objects.equals(user, other.user)
               && Objects.equals(organization, other.organization)
               && actions.equals(other.actions);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(user);
        hash = 31 * hash + Objects.hashCode(organization);
        hash = 31 * hash + actions.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "MemberImpl{" +
               "user='" + user + '\'' +
               ", organization='" + organization + '\'' +
               ", actions=" + actions +
               '}';
    }
}
