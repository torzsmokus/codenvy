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
package com.codenvy.ext.java.server;

import org.eclipse.che.commons.lang.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Retrieves csrf token and related session id from wsmaster.
 *
 * @author Max Shaposhnik
 */
public class CsrfTokenProvider implements Provider<CsrfTokenPair> {

    private static final Logger LOG = LoggerFactory.getLogger(CsrfTokenProvider.class);

    private CsrfTokenPair csrfTokenPair;

    private final String apiEndpoint;

    @Inject
    public CsrfTokenProvider(@Named("api.endpoint") String apiEndpoint) {
        this.apiEndpoint = apiEndpoint.endsWith("/") ? apiEndpoint : apiEndpoint + "/";
    }

    @Override
    public CsrfTokenPair get() {
        if (csrfTokenPair != null) {
            return csrfTokenPair;
        } else {
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection)new URL(apiEndpoint).openConnection();
                http.setRequestMethod("OPTIONS");
                http.setRequestProperty("X-CSRF-Token", "fetch");
                final int responseCode = http.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    LOG.warn("Can not retrieve csrf token by path: {}. Response status: {}. Error message: {}",
                             apiEndpoint.toString(), responseCode, IoUtil.readStream(http.getErrorStream()));
                    return null;
                }
                final String token = http.getHeaderField("X-CSRF-Token");
                final String cookiesHeader = http.getHeaderField(HttpHeaders.SET_COOKIE);
                if (token != null && cookiesHeader != null) {
                    for (HttpCookie cookie : HttpCookie.parse(cookiesHeader)) {
                        if (cookie.getName().equals("JSESSIONID")) {
                            csrfTokenPair = new CsrfTokenPair(token, cookie.getValue());
                            return csrfTokenPair;
                        }
                    }
                }
            } catch (IOException e) {
                LOG.warn(e.getLocalizedMessage(), e);
            } finally {
                if (http != null) {
                    http.disconnect();
                }
            }
        }
     return null;
    }
}
