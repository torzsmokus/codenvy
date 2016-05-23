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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Max Shaposhnik
 */
public class CsrfNonceProvider implements Provider<String> {

    private static final Logger LOG = LoggerFactory.getLogger(CsrfNonceProvider.class);

    private String csrfToken;

    private final String apiEndpoint;

    @Inject
    public CsrfNonceProvider(@Named("api.endpoint") String apiEndpoint) {
        this.apiEndpoint = apiEndpoint.endsWith("/") ? apiEndpoint : apiEndpoint + "/";
    }

    @Override
    public String get() {
        if (csrfToken != null) {
            return csrfToken;
        } else {
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection)new URL(apiEndpoint).openConnection();
                http.setRequestMethod("OPTIONS");
                http.setRequestProperty("X-CSRF-Token", "fetch");
                int responseCode = http.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    LOG.warn("Can not receive csrf token by path: {}. Response status: {}. Error message: {}",
                             apiEndpoint.toString(), responseCode, IoUtil.readStream(http.getErrorStream()));
                    return null;
                }
                csrfToken = http.getRequestProperty("X-CSRF-Token");
                return csrfToken;
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
