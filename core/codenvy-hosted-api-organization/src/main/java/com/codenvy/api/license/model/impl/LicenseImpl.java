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
package com.codenvy.api.license.model.impl;

import com.codenvy.api.license.model.License;
import com.codenvy.api.resources.model.ProvidedResources;
import com.codenvy.api.resources.model.impl.ResourceImpl;

import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class LicenseImpl implements License {
    private final String              owner;
    private final List<ResourceImpl>  resources;

    public LicenseImpl(String owner, List<ResourceImpl> resources) {
        this.owner = owner;
        this.resources = resources;
    }

    @Override
    public String getOwner() {
        return owner;
    }
    @Override
    public List<ResourceImpl> getResources() {
        return resources;
    }

    @Override
    public List<? extends ProvidedResources> getDetails() {
        return null;
    }

    //TODO Add hashCode, equals and toString methods
}
