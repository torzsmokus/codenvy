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
package com.codenvy.api.resources;

import com.codenvy.api.resources.model.Session;
import com.codenvy.api.resources.model.impl.SessionImpl;
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Persistence storage for {@link Session}
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class SessionDao {
    private final LocalStorage             localStorage;
    private final Map<String, SessionImpl> sessions;

    @Inject
    public SessionDao(LocalStorageFactory storageFactory) throws IOException {
        this.localStorage = storageFactory.create("sessions.json");
        this.sessions = new HashMap<>();
    }

    @PostConstruct
    public void load() {
        localStorage.loadMap(new TypeToken<Map<String, SessionImpl>>() {});
    }

    @PreDestroy
    public void save() throws IOException {
        localStorage.store(sessions);
    }

    /**
     * Stores session
     *
     * @param session
     *         session to store
     */
    public SessionImpl store(SessionImpl session) {
        sessions.put(session.getId(), session);
        return session;
    }

    /**
     * Get session by id
     *
     * @param id
     *         id of session
     */
    public SessionImpl get(String id) throws NotFoundException {
        final SessionImpl session = sessions.get(id);
        if (session == null) {
            throw new NotFoundException(String.format("Session with id '%s' was not found", id));
        }
        return session;
    }

    /**
     * @param id
     *         id of session
     * @param time
     *         new stop time
     * @param reason
     *         reason to update stop time
     */
    public void updateStop(String id, long time, String reason) throws NotFoundException {
        final SessionImpl session = sessions.get(id);
        if (session == null) {
            throw new NotFoundException(String.format("Session with id '%s' was not found", id));
        }
        session.setStopTime(time);
        session.setStopReason(reason);
    }
}
