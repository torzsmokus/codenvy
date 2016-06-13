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
package com.codenvy.api.resources.model.impl;

import com.codenvy.api.resources.model.Resource;
import com.codenvy.api.resources.model.Session;

import java.util.Set;

/**
 * @author Sergii Leschenko
 */
public class SessionImpl implements Session {
    private String        id;
    private String        user;
    private String        account;
    private String        workspace;
    private Set<Resource> resources;
    private long          startTime;
    private long          stopTime;
    private String        stopReason;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getWorkspace() {
        return workspace;
    }

    @Override
    public Set<Resource> getResources() {
        return resources;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getStopTime() {
        return stopTime;
    }

    @Override
    public String getStopReason() {
        return stopReason;
    }

    //TODO Add hashCode, equals and toString methods
}
