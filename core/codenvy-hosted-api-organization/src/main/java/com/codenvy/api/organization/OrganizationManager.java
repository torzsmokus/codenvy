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

import java.util.List;

/**
 * Facade for Organization related operations.
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class OrganizationManager {

    public Organization create(String name) {
        throw new UnsupportedOperationException();
    }

    public void remove(String organization) {
        throw new UnsupportedOperationException();
    }

    public Organization updateName(String organization, String name) {
        throw new UnsupportedOperationException();
    }

    public Member addMember(String user, String organization, List<String> actions) {
        throw new UnsupportedOperationException();
    }

    public void removeMember(String organization, String user) {
        throw new UnsupportedOperationException();
    }
}
