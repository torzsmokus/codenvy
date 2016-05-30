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

import org.eclipse.che.commons.annotation.Nullable;

import java.util.List;

/**
 * Describes group of users that can use shared resources
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface Organization {

    /**
     * Returns name of organization
     */
    String getName();

    /**
     * Returns list of members of organization
     */
    List<Member> getMembers();

    /**
     * Returns list of children organizations which can use parent's license
     */
    List<Organization> getChildren();

    /**
     * Returns parent organization or null if current organization doesn't have parent
     */
    @Nullable
    Organization getParent();

    /**
     * Returns whether the organization owns the licence (true) or the license was given by some other organization to use (false)
     */
    boolean isLicenseOwner();

    /**
     * Returns the license organization uses
     */
    License getLicense();
}
