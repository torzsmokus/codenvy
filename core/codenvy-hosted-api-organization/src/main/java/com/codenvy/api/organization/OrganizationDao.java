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
import com.codenvy.api.organization.model.impl.OrganizationImpl;
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sergii Leschenko
 */
public class OrganizationDao {
    private final Map<String, OrganizationImpl> organizations = new HashMap<>();
    private final LocalStorage organizationStorage;

    public OrganizationDao(LocalStorageFactory storageFactory) throws IOException {
        organizationStorage = storageFactory.create("organization.json");
    }

    @PostConstruct
    public void load() {
        organizations.putAll(organizationStorage.loadMap(new TypeToken<Map<String, OrganizationImpl>>() {}));
    }

    @PreDestroy
    public void save() throws IOException {
        organizationStorage.store(organizations);
    }


    public void create(OrganizationImpl organization) throws ConflictException {
        //TODO add name validation
        if (organizations.containsKey(organization.getId())) {
            throw new ConflictException("Organization with id '%s' already exists");
        }
        organizations.put(organization.getId(), organization);
    }

    public OrganizationImpl getById(String organizationId) throws NotFoundException {
        final OrganizationImpl organization = organizations.get(organizationId);
        if (organization == null) {
            throw new NotFoundException("Organization with id '%s' was not found");
        }
        return organization;
    }

    public List<Organization> getByParent(String parent) {
        return organizations.values()
                            .stream()
                            .filter(org -> parent.equals(org.getParent()))
                            .collect(Collectors.toList());
    }

    public void remove(String organization) throws NotFoundException {
        final OrganizationImpl removedOrg = organizations.remove(organization);
        if (removedOrg == null) {
            throw new NotFoundException("Organization with id '%s' was not found");
        }
    }
}
