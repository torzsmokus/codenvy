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

import com.codenvy.api.license.License;

import java.util.List;

/**
 * @author gazarenkov
 */
public class OrganizationManager {

    public Organization create(String name, License license, String primaryUserId) {
        return null;
    }

    public void remove(String organizationId) {

    }

    public Organization updateName(String name) {
        return null;
    }

    public Organization updateLicense(License license) {
        return null;
    }


    public Member addMember(String userId, String organizationId, List<String> actions) {
        return null;
    }

    public void removeMember(String organization, String user) {

    }
}
