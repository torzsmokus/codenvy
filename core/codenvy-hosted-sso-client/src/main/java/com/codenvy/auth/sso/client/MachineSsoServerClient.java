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

import com.codenvy.api.dao.authentication.AccessTicket;
import com.codenvy.api.dao.authentication.TicketManager;
import com.codenvy.machine.authentication.server.MachineTokenRegistry;
import com.google.common.collect.ImmutableSet;
import com.google.inject.name.Named;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.user.server.UserService;
import org.eclipse.che.api.user.shared.dto.UserDescriptor;
import org.eclipse.che.commons.user.User;
import org.eclipse.che.commons.user.UserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Optional;

/**
 * Retrieves master {@link User} based on the machine token
 * Machine token detection is simple and based on the machine token prefix,
 * so if token is prefixed with 'machine' then the mechanism is triggered
 * otherwise method call delegated to the super {@link HttpSsoServerClient#getUser(String, String, String, String)}.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class MachineSsoServerClient extends HttpSsoServerClient {
    private static final Logger LOG = LoggerFactory.getLogger(MachineSsoServerClient.class);

    private final MachineTokenRegistry tokenRegistry;
    private final TicketManager        ticketManager;

    @Inject
    public MachineSsoServerClient(@Named("api.endpoint") String apiEndpoint,
                                  HttpJsonRequestFactory requestFactory,
                                  MachineTokenRegistry tokenRegistry,
                                  TicketManager ticketManager) {
        super(apiEndpoint, requestFactory);
        this.tokenRegistry = tokenRegistry;
        this.ticketManager = ticketManager;
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
            final Optional<AccessTicket> ticket = ticketManager.getAccessTickets()
                                                               .stream()
                                                               .filter(t -> t.getPrincipal().getId().equals(user.getId()))
                                                               .findAny();
            if (ticket.isPresent()) {
                return new UserImpl(user.getName(),
                                    user.getId(),
                                    ticket.get().getAccessToken(),
                                    ImmutableSet.of("user"),
                                    false);
            }
        } catch (ApiException | IOException ex) {
            LOG.warn(ex.getLocalizedMessage(), ex);
        }
        return null;
    }
}
