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

import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sergii Leschenko
 */
public class PromotionDao {
    private final Map<String, PromotionImpl> promotions;
    private final LocalStorage               promotionStorage;

    @Inject
    public PromotionDao(LocalStorageFactory storageFactory) throws IOException {
        this.promotions = new HashMap<>();
        this.promotionStorage = storageFactory.create("promotions.json");
    }

    @PostConstruct
    public void load() {
        promotionStorage.loadMap(new TypeToken<Map<String, PromotionImpl>>() {});
    }

    @PreDestroy
    public void save() throws IOException {
        promotionStorage.store(promotions);
    }


    public void store(PromotionImpl promotion) {
        promotions.put(promotion.getId(), promotion);
    }

    public List<PromotionImpl> getByOwner(String owner) {
        return promotions.values()
                         .stream()
                         .filter(promotion -> promotion.getOwner().equals(owner))
                         .collect(Collectors.toList());
    }
}
