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
package com.codenvy.machine.authentication.server;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.user.shared.dto.UserDescriptor;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.user.User;
import org.eclipse.che.commons.user.UserImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Protects user's machine from unauthorized access.
 *
 * @author Anton Korneta
 */
@Singleton
public class MachineLoginFilter implements Filter {

    private final String                 userToken;
    private final String                 tokenServiceEndpoint;
    private final HttpJsonRequestFactory requestFactory;

    @Inject
    public MachineLoginFilter(@Named("user.token") String userToken,
                              @Named("api.endpoint") String apiEndpoint,
                              HttpJsonRequestFactory requestFactory) {
        this.userToken = userToken;
        this.tokenServiceEndpoint = apiEndpoint + "/machine/token";
        this.requestFactory = requestFactory;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest)request;
        final HttpSession session = httpReq.getSession(false);
        if (session != null) {
            EnvironmentContext.getCurrent().setUser((User)session.getAttribute("principal"));
            chain.doFilter(request, response);
            return;
        }
        final String machineToken = extractToken(request);
        if (isNullOrEmpty(machineToken)) {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                                      "Authentication on machine failed, token is missed");
            return;
        }
        // TODO put user in to the context
        try {
            final UserDescriptor user = requestFactory.fromUrl(tokenServiceEndpoint + "/user/" + extractToken(request))
                                                      .useGetMethod()
                                                      .setAuthorizationHeader(machineToken)
                                                      .request()
                                                      .asDto(UserDescriptor.class);
            final User machineUser = new UserImpl(user.getName(), user.getId(), machineToken, null, false);
            EnvironmentContext.getCurrent().setUser(machineUser);
            final HttpSession httpSession = httpReq.getSession(true);
            httpSession.setAttribute("principal", machineUser);
            chain.doFilter(request, response);
        } catch (NotFoundException nfEx) {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                                      "Authentication on machine failed, token " + machineToken + " is invalid");
        } catch (ApiException apiEx) {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, apiEx.getMessage());
        }
    }

    @Override
    public void destroy() {

    }

    private String extractToken(ServletRequest request) {
        final HttpServletRequest req = (HttpServletRequest)request;
        final String accessToken = "access_token=";
        final String query = req.getQueryString();
        if (query != null) {
            final int start = query.indexOf('&' + accessToken);
            if (start != -1 || query.startsWith(accessToken)) {
                final int begin = start + accessToken.length() + 1;
                int end = query.indexOf('&', begin);
                if (end == -1) {
                    end = query.length();
                }
                if (end != begin) {
                    return query.substring(begin, end);
                }
            }
        }

        final String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isNullOrEmpty(header)) {
            final String[] split = header.split(" ");
            if (split.length > 1) {
                return split[1];
            }
            return split[0];
        }
        return "";
    }
}
