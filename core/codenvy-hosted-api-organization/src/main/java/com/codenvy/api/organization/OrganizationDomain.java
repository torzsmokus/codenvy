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

import com.codenvy.api.permission.server.AbstractPermissionsDomain;

import java.util.Arrays;

/**
 * @author Sergii Leschenko
 */
public class OrganizationDomain extends AbstractPermissionsDomain {
    public static String DOMAIN_ID = "organization";

    public static String DELETE                 = "delete";
    public static String MANAGE_WORKSPACES      = "manageWorkspaces";
    public static String MANAGE_SUBORANIZATIONS = "manageSuborganizations";

    protected OrganizationDomain() {
        super(DOMAIN_ID, Arrays.asList(DELETE,
                                       MANAGE_WORKSPACES,
                                       MANAGE_SUBORANIZATIONS));
    }
}
