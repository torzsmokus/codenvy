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

import com.codenvy.api.organization.model.Organization;

import java.util.Objects;

/**
 * @author Sergii Leschenko
 */
public class OrganizationImpl implements Organization {
    private String name;
    private String id;
    private String parent;

    public OrganizationImpl(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public OrganizationImpl(String name, String id, String parent) {
        this.name = name;
        this.id = id;
        this.parent = parent;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrganizationImpl)) return false;
        final OrganizationImpl other = (OrganizationImpl)obj;
        return Objects.equals(name, other.name)
               && Objects.equals(id, other.id)
               && Objects.equals(parent, other.parent);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(name);
        hash = 31 * hash + Objects.hashCode(id);
        hash = 31 * hash + Objects.hashCode(parent);
        return hash;
    }

    @Override
    public String toString() {
        return "OrganizationImpl{" +
               "name='" + name + '\'' +
               ", id='" + id + '\'' +
               ", parent='" + parent + '\'' +
               '}';
    }
}
