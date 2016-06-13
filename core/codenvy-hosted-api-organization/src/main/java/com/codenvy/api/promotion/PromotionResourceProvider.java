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

import com.codenvy.api.resources.ResourcesProvider;
import com.codenvy.api.resources.model.Resource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class PromotionResourceProvider implements ResourcesProvider {
    private final PromotionManager promotionManager;

    @Inject
    public PromotionResourceProvider(PromotionManager promotionManager) {
        this.promotionManager = promotionManager;
    }

    @Override
    public List<Resource> getAvailableResources(String account) {
        final List<Promotion> promotions = promotionManager.getByOwner(account);
        Map<String, Resource> resources = new HashMap<>();
        for (Promotion promotion : promotions) {
            for (Resource resource : promotion.getResources()) {
                //TODO calculate sum of each type of resources
                resources.put(resource.getType(), resource);
            }
        }
        return new ArrayList<>(resources.values());
    }
}
