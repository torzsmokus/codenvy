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
package com.codenvy.api.promotion;

import com.codenvy.api.resources.model.Resource;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public interface Promotion {
    /**
     * Returns id of license
     */
    String getId();

    /**
     * Returns owner of license
     */
    String getOwner();

    /**
     * Returns cause why account was provided by promotion
     */
    String getCause();

    /**
     * Returns time when license became active
     */
    long getStartTime();

    /**
     * Returns time when license will be/became inactive
     */
    long getEndTime();

    /**
     * Returns list of resources which can be used by owner
     */
    List<Resource> getResources();
}
