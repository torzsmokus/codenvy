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
import com.codenvy.api.shared.dto.OrganizationDto;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Sergii Leschenko
 */
@Path("/organization")
public class OrganizationService extends Service {
    private final OrganizationManager organizationManager;

    @Inject
    public OrganizationService(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    /**
     * Create new organization with given name
     *
     * @param name
     *         name of organization
     * @return created organization
     */
    @POST
    public OrganizationDto create(@QueryParam("name") String name) throws ConflictException {
        return toDto(organizationManager.create(name));
    }

    /**
     * Returns organization by given id
     *
     * @param organization
     *         id of organization
     */
    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{organization}")
    public OrganizationDto getById(@PathParam("organization") String organization) throws NotFoundException {
        return toDto(organizationManager.getById(organization));
    }

    /**
     * Returns organizations for current user
     */
    @GET
    @Produces(APPLICATION_JSON)
    public List<OrganizationDto> get() throws NotFoundException {
        return organizationManager.getByMember(EnvironmentContext.getCurrent().getSubject().getUserId())
                                  .stream()
                                  .map(this::toDto)
                                  .collect(Collectors.toList());
    }

    /**
     * Remove organization
     *
     * @param organization
     *         id of organization that should be removed
     */
    @DELETE
    @Path("/{id}")
    public void remove(@PathParam("id") String organization) throws NotFoundException {
        organizationManager.remove(organization);
    }

    private OrganizationDto toDto(Organization organization) {
        return DtoFactory.newDto(OrganizationDto.class)
                         .withId(organization.getId())
                         .withName(organization.getName())
                         .withParent(organization.getParent());
    }
}
