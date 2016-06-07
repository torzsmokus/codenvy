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

import com.codenvy.api.organization.model.Member;
import com.codenvy.api.organization.model.Organization;

import java.util.List;

/**
 * Facade for Organization related operations.
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class OrganizationManager {

    /**
     * Create new organization with given name
     *
     * @param name
     *         name of organization
     * @return created organization
     */
    public Organization create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns organization by given id
     *
     * @param organization
     *         id of organization
     */
    public Organization getById(String organization) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns subOrganization by given parent organization
     *
     * @param parent
     *         id of parent organization
     */
    public List<Organization> getByParent(String parent) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove organization
     *
     * @param organization
     *         id of organization that should be removed
     */
    public void remove(String organization) {
        throw new UnsupportedOperationException();
    }

    /**
     * Stores (adds or updates) member.
     *
     * @param user
     *         id of user
     * @param organization
     *         id of organization
     * @param actions
     *         list of actions which can be performed by user
     * @return created member
     */
    public Member storeMember(String user, String organization, List<String> actions) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns all members of given organization
     *
     * @param organization
     *         id of organization
     */
    public List<Member> getMembers(String organization) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns all members for given user
     *
     * @param user
     *         id of user
     */
    public List<Member> getMemberships(String user) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove member with given organization and user
     *
     * @param organization
     *         id of organization
     * @param user
     *         id of user
     */
    public void removeMember(String organization, String user) {
        throw new UnsupportedOperationException();
    }
}
