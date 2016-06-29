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

import com.codenvy.api.resources.model.Session;
import com.google.common.base.Objects;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public class SessionImpl implements Session {
    private String             id;
    private String             user;
    private String             account;
    private String             workspace;
    private List<ResourceImpl> resources;
    private long               startTime;
    private long               stopTime;
    private String             stopReason;

    public SessionImpl() {
    }

    public SessionImpl(String id, String user, String account, String workspace,
                       List<ResourceImpl> resources, long startTime, long stopTime, String stopReason) {
        this.id = id;
        this.user = user;
        this.account = account;
        this.workspace = workspace;
        this.resources = resources;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.stopReason = stopReason;
    }

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
    public List<ResourceImpl> getResources() {
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

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionImpl)) return false;
        SessionImpl session = (SessionImpl)o;
        return Objects.equal(startTime, session.startTime) &&
               Objects.equal(stopTime, session.stopTime) &&
               Objects.equal(id, session.id) &&
               Objects.equal(user, session.user) &&
               Objects.equal(account, session.account) &&
               Objects.equal(workspace, session.workspace) &&
               Objects.equal(resources, session.resources) &&
               Objects.equal(stopReason, session.stopReason);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, user, account, workspace, resources, startTime, stopTime, stopReason);
    }

    @Override
    public String toString() {
        return "SessionImpl{" +
               "id='" + id + '\'' +
               ", user='" + user + '\'' +
               ", account='" + account + '\'' +
               ", workspace='" + workspace + '\'' +
               ", resources=" + resources +
               ", startTime=" + startTime +
               ", stopTime=" + stopTime +
               ", stopReason='" + stopReason + '\'' +
               '}';
    }
}
