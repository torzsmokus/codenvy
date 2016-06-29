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

import com.codenvy.api.resources.model.impl.ResourceImpl;
import com.google.common.base.Objects;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public class PromotionImpl implements Promotion {
    private final String             id;
    private final String             owner;
    private final String             cause;
    private final long               startTime;
    private final long               endTime;
    private final List<ResourceImpl> resources;

    public PromotionImpl(String id,
                         String owner,
                         String cause,
                         long startTime,
                         long endTime,
                         List<ResourceImpl> resources) {
        this.id = id;
        this.owner = owner;
        this.cause = cause;
        this.startTime = startTime;
        this.endTime = endTime;
        this.resources = resources;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getCause() {
        return cause;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public List<ResourceImpl> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromotionImpl)) return false;
        PromotionImpl promotion = (PromotionImpl)o;
        return Objects.equal(startTime, promotion.startTime) &&
               Objects.equal(endTime, promotion.endTime) &&
               Objects.equal(id, promotion.id) &&
               Objects.equal(owner, promotion.owner) &&
               Objects.equal(cause, promotion.cause) &&
               Objects.equal(resources, promotion.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, owner, cause, startTime, endTime, resources);
    }

    @Override
    public String toString() {
        return "PromotionImpl{" +
               "id='" + id + '\'' +
               ", owner='" + owner + '\'' +
               ", cause='" + cause + '\'' +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", resources=" + resources +
               '}';
    }
}
