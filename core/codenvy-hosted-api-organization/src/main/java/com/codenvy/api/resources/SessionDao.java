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

/**
 * Persistence storage for {@link Session}
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface SessionDao {

    /**
     * Stores session
     *
     * @param session
     *         session to store
     */
    Session store(Session session);

    /**
     * Get session by id
     *
     * @param id
     *         id of session
     */
    Session get(String id);

    /**
     * @param id
     *         id of session
     * @param time
     *         new stop time
     * @param reason
     *         reason to update stop time
     */
    void updateStop(String id, long time, String reason);
}
