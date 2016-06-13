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

    //TODO Add hashCode, equals and toString methods
}
