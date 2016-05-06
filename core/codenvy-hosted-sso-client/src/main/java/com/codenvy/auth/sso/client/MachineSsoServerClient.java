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
package com.codenvy.auth.sso.client;

import com.codenvy.machine.authentication.server.MachineTokenRegistry;
import com.google.common.collect.ImmutableSet;
import com.google.inject.name.Named;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.user.server.UserService;
import org.eclipse.che.api.user.shared.dto.UserDescriptor;
import org.eclipse.che.api.workspace.shared.dto.event.WorkspaceStatusEvent;
import org.eclipse.che.commons.user.User;
import org.eclipse.che.commons.user.UserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

@Singleton
public class MachineSsoServerClient extends HttpSsoServerClient {
    private static final Logger LOG = LoggerFactory.getLogger(MachineSsoServerClient.class);

    private final MachineTokenRegistry tokenRegistry;

    @Inject
    public MachineSsoServerClient(@Named("api.endpoint") String apiEndpoint,
                                  HttpJsonRequestFactory requestFactory,
                                  MachineTokenRegistry tokenRegistry,
                                  SessionStore sessionStore,
                                  EventService eventService) {
        super(apiEndpoint, requestFactory);
        this.tokenRegistry = tokenRegistry;
        eventService.subscribe(new EventSubscriber<WorkspaceStatusEvent>() {
            @Override
            public void onEvent(WorkspaceStatusEvent event) {
                if (event.getEventType().equals(WorkspaceStatusEvent.EventType.STOPPED)) {
                    tokenRegistry.removeTokens(event.getWorkspaceId())
                                 .values()
                                 .forEach(sessionStore::removeSessionByToken);
                }
            }
        });
    }

    @Override
    public User getUser(String token, String clientUrl, String workspaceId, String accountId) {
        if (!token.startsWith("machine")) {
            return super.getUser(token, clientUrl, workspaceId, accountId);
        }
        try {
            final UserDescriptor user = requestFactory.fromUrl(UriBuilder.fromUri(apiEndpoint)
                                                                         .path(UserService.class)
                                                                         .path(UserService.class, "getById")
                                                                         .build(tokenRegistry.getUserId(token))
                                                                         .toString())
                                                      .useGetMethod()
                                                      .request()
                                                      .asDto(UserDescriptor.class);
            return new UserImpl(user.getName(), user.getId(), token, ImmutableSet.of("user"), false);
        } catch (ApiException | IOException ex) {
            LOG.warn(ex.getLocalizedMessage(), ex);
        }
        return null;
    }
}
