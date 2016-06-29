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
package com.codenvy.api.license;

import com.codenvy.api.resources.ResourcesProvider;
import com.codenvy.api.resources.model.Resource;
import com.codenvy.api.resources.model.impl.ResourceImpl;

import org.eclipse.che.api.core.NotFoundException;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Sergii Leschenko
 */
public class LicenseResourcesProvider implements ResourcesProvider {
    private final LicenseDao licenseDao;

    @Inject
    public LicenseResourcesProvider(LicenseDao licenseDao) {
        this.licenseDao = licenseDao;
    }

    @Override
    public List<ResourceImpl> getAvailableResources(String accountId) throws NotFoundException {
        return licenseDao.getByOwner(accountId).getResources();
    }
}
