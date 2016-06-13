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
package com.codenvy.api.resources;

import com.codenvy.api.resources.model.Resource;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class ResourcesManager {
    private final List<ResourcesProvider>  resourcesProviders;
    private final Map<String, Integer>     accountToUsedRAM;
    private final Multimap<String, String> organizationToParents;

    @Inject
    public ResourcesManager(List<ResourcesProvider> resourcesProviders) {
        this.resourcesProviders = resourcesProviders;
        this.accountToUsedRAM = new HashMap<>();
        this.organizationToParents = HashMultimap.create();
    }

    /**
     * Returns list of resources which are available for given account
     *
     * @param account
     *         id of account
     */
    public List<Resource> getAvailableResources(String account) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns list of resources which are used by given account
     *
     * @param account
     *         id of account
     */
    public List<Resource> getUsedResources(String account) {
        throw new UnsupportedOperationException();
    }
}
