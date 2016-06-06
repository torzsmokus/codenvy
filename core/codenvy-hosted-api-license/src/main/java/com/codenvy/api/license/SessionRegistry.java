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
package com.codenvy.api.license;

import com.codenvy.api.license.data.SessionData;
import com.codenvy.api.license.model.Resource;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.commons.lang.NameGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gazarenkov
 */
@Singleton
public class SessionRegistry {
    private final Map<String, ActiveSession> activeSessions;
    private final SessionDao                 sessionDao;

    @Inject
    public SessionRegistry(SessionDao sessionDao) {
        this.activeSessions = new HashMap<>();
        this.sessionDao = sessionDao;
    }

    public ActiveSession getActive(String id) throws NotFoundException {
        ActiveSession session = activeSessions.get(id);
        if (session == null)
            throw new NotFoundException("Session not found " + id);

        return session;
    }

    public SessionData getStored(String id) throws NotFoundException {
        SessionData data = sessionDao.get(id);
        if (data == null) {
            throw new NotFoundException("Session not found " + id);
        }
        return data;
    }

    public ActiveSession add(Identity identity, Set<Resource> resources) throws NotFoundException {
        String id = NameGenerator.generate("license", 16);
        //TODO do we need to have license in session. Maybe to know why user can use resources.
        //TODO But it is possible only if identit'll have only one active license
        //TODO Fix identity.getId and null instead of license
        ActiveSession session = new ActiveSession(id, identity.getId(), null, System.currentTimeMillis(), resources);
        activeSessions.put(id, session);
        sessionDao.add(session);
        return session;
    }

    public void tick(String sessionId) {
        //TODO Maybe update action license instance
        sessionDao.setStop(sessionId, System.currentTimeMillis(), "tick");
    }

    public void stop(String sessionId) {
        ActiveSession session = activeSessions.get(sessionId);
        if (session != null) {
            sessionDao.setStop(sessionId, System.currentTimeMillis(), "setStop");
        }
        activeSessions.remove(sessionId);
    }

    public List<ActiveSession> getActiveByType(String licenseType) {
        return activeSessions.values()
                             .stream()
                             .filter(session -> session.getLicense().getType().getId().equals(licenseType))
                             .collect(Collectors.toList());
    }
}
