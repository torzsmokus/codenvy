package com.codenvy.api.organization;

import com.codenvy.api.permission.server.PermissionsDomain;
import com.google.common.collect.ImmutableSet;

/**
 * @author Sergii Leschenko
 */
public class OrganizationPermissionsDomain extends PermissionsDomain {
    public static final String DOMAIN_ID = "organization";

    public static final String CREATE_WORKSPACES       = "createWorkspaces";
    public static final String MANAGE_WORKSPACES       = "manageWorkspaces";
    public static final String MANAGE_SUBORGANIZATIONS = "manageSuborganizations";

    public OrganizationPermissionsDomain() {
        super(DOMAIN_ID, ImmutableSet.of(SET_PERMISSIONS,
                                         READ_PERMISSIONS,
                                         CREATE_WORKSPACES,
                                         MANAGE_WORKSPACES,
                                         MANAGE_SUBORGANIZATIONS));
    }
}
