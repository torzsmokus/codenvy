/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
