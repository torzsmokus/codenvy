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
package com.codenvy.api.resources.model.impl;

import com.codenvy.api.resources.model.Resource;

/**
 * @author Sergii Leschenko
 */
public class ResourcesImpl implements Resource {
    private String unit;
    private String type;
    private int    amount;

    public ResourcesImpl(String unit, String type, int amount) {
        this.unit = unit;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    //TODO Add hashCode, equals and toString methods
}
