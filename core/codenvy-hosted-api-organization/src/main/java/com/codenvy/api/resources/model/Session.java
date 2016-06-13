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
package com.codenvy.api.resources.model;

import java.util.Set;

/**
 * Describes resources usage session
 *
 * @author gazarenkov
 */
public interface Session {
    /**
     * Returns id of session
     */
    String getId();

    /**
     * Return id of user that init start of session
     */
    String getUser();

    /**
     * Returns account id
     */
    String getAccount();

    /**
     * Returns workspace id that use resources
     */
    String getWorkspace();

    /**
     * Returns list of resources which are used in this session
     */
    Set<Resource> getResources();

    /**
     * Returns timestamp of session start
     */
    long getStartTime();

    /**
     * Returns timestamp of session stop
     *
     * <p>It should have value even when session is active
     * Then it will be contain time of last usage registration
     */
    long getStopTime();

    /**
     * Returns reason of stopping session, e.g. 'Tick' or 'Stopped by user' or 'License expired'
     */
    String getStopReason();
}
