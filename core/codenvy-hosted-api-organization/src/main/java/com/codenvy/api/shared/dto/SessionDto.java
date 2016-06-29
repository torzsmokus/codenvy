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
package com.codenvy.api.shared.dto;

import com.codenvy.api.resources.model.Session;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
@DTO
public interface SessionDto extends Session {
    @Override
    String getId();

    void setId(String id);

    SessionDto withId(String id);

    @Override
    String getUser();

    void setUser(String user);

    SessionDto withUser(String user);

    @Override
    String getAccount();

    void setAccount(String account);

    SessionDto withAccount(String account);

    @Override
    String getWorkspace();

    void setWorkspace(String workspace);

    SessionDto withWorkspace(String workspace);

    @Override
    List<ResourceDto> getResources();

    void setResources(List<ResourceDto> resources);

    SessionDto withResources(List<ResourceDto> resources);

    @Override
    long getStartTime();

    void setStartTime(long startTime);

    SessionDto withStartTime(long startTime);

    @Override
    long getStopTime();

    void setStopTime(long stopTime);

    SessionDto withStopTime(long stopTime);

    @Override
    String getStopReason();

    void setStopReason(String stopReason);

    SessionDto withStopReason(String stopReason);
}
