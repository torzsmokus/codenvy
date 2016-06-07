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

/**
 * Defines data access object contract for {@link License}.
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface LicenseDao {
    /**
     * Adds license
     */
    void create(License license);

    /**
     * Deletes license by id
     *
     * @param id
     *         id of license
     */
    void delete(String id);

    /**
     * Returns license by id
     *
     * @param id
     *         the id
     * @return license data
     */
    License get(String id);

    /**
     * Returns license by owner
     *
     * @param owner
     *         id of account owner
     * @return license data
     */
    License getByOwner(String owner);
}
