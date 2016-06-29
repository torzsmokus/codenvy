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
import com.codenvy.api.resources.model.impl.ResourceImpl;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.codenvy.api.resources.ResourceHelper.groupResources;

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
    public List<ResourceImpl> getAvailableResources(String account) {
        final List<PromotionImpl> promotions = promotionManager.getByOwner(account);
        final List<ResourceImpl> allResources = promotions.stream()
                                                          .flatMap(promotion -> promotion.getResources().stream())
                                                          .collect(Collectors.toList());
        return groupResources(allResources);
    }
}
