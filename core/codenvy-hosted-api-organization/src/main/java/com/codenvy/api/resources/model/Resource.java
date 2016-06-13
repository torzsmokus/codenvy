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

/**
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface Resource {
    /**
     * Returns type of resources, e.g. RAM
     */
    String getType();

    /**
     * Returns amount of resources
     */
    int getAmount();

    /**
     * Returns unit of resources, e.g. for RAM type it should be equal to mb
     *
     * <p>Note: resources that has the same type should have the same unit
     */
    String getUnit();
}
