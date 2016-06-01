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
package com.codenvy.api.license.data;

import com.codenvy.api.license.Identity;

/**
 * @author gazarenkov
 */
public class LicenseData {
    private String   id;
    private String   typeId;
    private Identity owner;
    private long     startTime;
    private long     endTime;

    public LicenseData(String id, String typeId, Identity owner, long startTime, long endTime) {
        this.id = id;
        this.typeId = typeId;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public String getTypeId() {
        return typeId;
    }

    public Identity getOwner() {
        return owner;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
