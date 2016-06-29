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
package com.codenvy.api.resources;

import com.codenvy.api.resources.model.Resource;
import com.codenvy.api.resources.model.Session;
import com.codenvy.api.shared.dto.ResourceDto;
import com.codenvy.api.shared.dto.SessionDto;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergii Leschenko
 */
@Path("/resources")
public class ResourcesService extends Service {
    private final UsageManager usageManager;

    @Inject
    public ResourcesService(UsageManager usageManager) {
        this.usageManager = usageManager;
    }

    @GET
    @Path("/{account}")
    public List<ResourceDto> getAvailableResources(@PathParam("account")
                                                   String accountId) throws NotFoundException, ServerException {
        return usageManager.getAvailableResources(accountId)
                           .stream()
                           .map(this::toDto)
                           .collect(Collectors.toList());
    }

    @GET
    @Path("/{account}/session")
    public List<SessionDto> getActiveSessions(@PathParam("account")
                                              String accountId) throws NotFoundException {
        return usageManager.getActiveSessions(accountId)
                           .stream()
                           .map(this::toDto)
                           .collect(Collectors.toList());
    }

    private SessionDto toDto(Session session) {
        return DtoFactory.newDto(SessionDto.class)
                         .withId(session.getId())
                         .withAccount(session.getAccount())
                         .withUser(session.getUser())
                         .withResources(session.getResources()
                                               .stream()
                                               .map(this::toDto)
                                               .collect(Collectors.toList()))
                         .withStartTime(session.getStartTime())
                         .withStopTime(session.getStopTime())
                         .withStopReason(session.getStopReason());
    }

    private ResourceDto toDto(Resource resource) {
        return DtoFactory.newDto(ResourceDto.class)
                         .withAmount(resource.getAmount())
                         .withType(resource.getType())
                         .withUnit(resource.getUnit());
    }
}
