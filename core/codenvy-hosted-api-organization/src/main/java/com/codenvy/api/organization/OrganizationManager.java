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

import com.codenvy.api.organization.model.Organization;
import com.codenvy.api.organization.model.impl.MemberImpl;
import com.codenvy.api.organization.model.impl.OrganizationImpl;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.commons.lang.NameGenerator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade for Organization related operations.
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class OrganizationManager {

    private final OrganizationDao organizationDao;
    private final MemberDao       memberDao;

    @Inject
    public OrganizationManager(OrganizationDao organizationDao, MemberDao memberDao) {
        this.organizationDao = organizationDao;
        this.memberDao = memberDao;
    }

    /**
     * Create new organization with given name
     *
     * @param name
     *         name of organization
     * @return created organization
     */
    public OrganizationImpl create(String name) throws ConflictException {
        final OrganizationImpl organization = new OrganizationImpl(name, NameGenerator.generate("organization", 16), null);
        organizationDao.create(organization);
        return organization;
    }

    /**
     * Returns organization by given id
     *
     * @param organization
     *         id of organization
     */
    public OrganizationImpl getById(String organization) throws NotFoundException {
        return organizationDao.getById(organization);
    }

    /**
     * Returns subOrganization by given parent organization
     *
     * @param parent
     *         id of parent organization
     */
    public List<Organization> getByParent(String parent) {
        return organizationDao.getByParent(parent);
    }

    /**
     * Remove organization
     *
     * @param organization
     *         id of organization that should be removed
     */
    public void remove(String organization) throws NotFoundException {
        organizationDao.remove(organization);
    }

    public List<OrganizationImpl> getByMember(String userId) {
        final List<String> organizationsIds = memberDao.getMemberships(userId)
                                                       .stream()
                                                       .map(MemberImpl::getOrganization)
                                                       .collect(Collectors.toList());
        List<OrganizationImpl> result = new ArrayList<>();
        for (String organizationsId : organizationsIds) {
            try {
                result.add(organizationDao.getById(organizationsId));
            } catch (NotFoundException e) {
                //TODO Write warning
            }
        }
        return result;
    }
}
