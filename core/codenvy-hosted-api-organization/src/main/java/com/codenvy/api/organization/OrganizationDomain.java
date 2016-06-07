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

import com.codenvy.api.permission.server.PermissionsDomain;
import com.google.common.collect.ImmutableSet;

/**
 * Describe permissions
 *
 * @author Sergii Leschenko
 */
public class OrganizationDomain extends PermissionsDomain {
    public static final String DOMAIN_ID = "organization";

    public static final String CREATE_WORKSPACES       = "createWorkspaces";
    public static final String MANAGE_WORKSPACES       = "manageWorkspaces";
    public static final String MANAGE_SUBORGANIZATIONS = "manageSuborganizations";

    //TODO Mb add manageAccount action to grant user perform operation with account like buying license

    public OrganizationDomain() {
        super(DOMAIN_ID, ImmutableSet.of(SET_PERMISSIONS,
                                         READ_PERMISSIONS,
                                         CREATE_WORKSPACES,
                                         MANAGE_WORKSPACES,
                                         MANAGE_SUBORGANIZATIONS));
    }
}
