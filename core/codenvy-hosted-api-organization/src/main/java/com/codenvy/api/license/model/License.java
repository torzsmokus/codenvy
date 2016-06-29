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
package com.codenvy.api.license.model;

import com.codenvy.api.resources.model.Resource;

import java.util.List;
import java.util.Map;

/**
 * Permits account use some resources.
 *
 * <p>There should by only one active license for one account
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface License {

    /**
     * Returns id of license
     */
    String getId();

    /**
     * Returns id of account owner
     */
    String getOwner();

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
    List<? extends Resource> getResources();

    /**
     * Returns license's attributes
     *
     * <p>It can be used for storing additional options of license.
     * For example, always on
     */
    Map<String, String> getAttributes();
}
