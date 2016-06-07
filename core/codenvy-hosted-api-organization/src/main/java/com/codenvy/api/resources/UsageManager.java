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

import org.eclipse.che.api.core.ServerException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Responsible for registering resources consuming
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
@Singleton
public class UsageManager {

    @Inject
    public UsageManager() {
    }

    /**
     * called by resource consumer (machine service)
     *
     * @param user
     *         id of user that init start of resources consuming
     * @param workspace
     *         id of workspace that start consume resources
     */
    //TODO Should we start session before workspace starting?
    //TODO Or maybe we should allocate resources and start session only after starting workspace
    public Session start(String user, String workspace) throws NotEnoughResourcesException,
                                                               ServerException {
        throw new UnsupportedOperationException();
    }

    /**
     * Should be called to notify that session in workspace is still active
     *
     * @param workspace
     *         id of workspace
     */
    public void tick(String workspace) {
        throw new UnsupportedOperationException();
    }

    /**
     * Should be called to notify that session in workspace is still stopped
     *
     * @param workspace
     *         id of workspace
     */
    public void stop(String workspace) {
        throw new UnsupportedOperationException();
    }
}
