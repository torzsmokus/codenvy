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
package com.codenvy.api.organization.model.impl;

import com.codenvy.api.organization.model.Limit;
import com.codenvy.api.resources.model.impl.ResourceImpl;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class LimitImpl implements Limit {
    private List<ResourceImpl>  resources;
    private Map<String, String> options;

    public LimitImpl(List<ResourceImpl> resources, Map<String, String> options) {
        this.resources = resources;
        this.options = options;
    }

    @Override
    public List<ResourceImpl> getResources() {
        return resources;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimitImpl)) return false;
        LimitImpl limit = (LimitImpl)o;
        return Objects.equal(resources, limit.resources) &&
               Objects.equal(options, limit.options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources, options);
    }

    @Override
    public String toString() {
        return "LimitImpl{" +
               "resources=" + resources +
               ", options=" + options +
               '}';
    }
}
