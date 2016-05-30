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

import com.codenvy.api.license.data.LicenseData;

/**
 * Defines data access object contract for {@link LicenseData}.
 *
 * @author gazarenkov
 * @author Sergii Lescheni
 */
public interface LicenseDao {
    /**
     * Adds license
     */
    void create(LicenseData license);

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
    LicenseData get(String id);

    /**
     * Returns license by owner
     *
     * @param owner
     *         id of owner
     * @return license data
     */
    //TODO id of owner is unique for all owners' types or we should add owner type to parameter?
    LicenseData getByOwner(String owner);
}
