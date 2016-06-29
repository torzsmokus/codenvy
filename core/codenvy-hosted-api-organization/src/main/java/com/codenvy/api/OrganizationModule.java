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
package com.codenvy.api;

import com.codenvy.api.license.LicenseResourcesProvider;
import com.codenvy.api.organization.OrganizationService;
import com.codenvy.api.promotion.PromotionResourceProvider;
import com.codenvy.api.resources.ResourcesProvider;
import com.codenvy.api.resources.ResourcesService;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * @author Sergii Leschenko
 */
public class OrganizationModule extends AbstractModule {
    @Override
    protected void configure() {
        //TODO remove it
        //temporary binding for usage im memory implementation in codenvy
        bind(String.class).annotatedWith(Names.named("che.conf.storage")).toInstance("/home/codenvy/codenvy-data/storage");

        bind(OrganizationService.class);
        bind(ResourcesService.class);

        Multibinder<ResourcesProvider> resourcesProviderBinder = Multibinder.newSetBinder(binder(), ResourcesProvider.class);
        resourcesProviderBinder.addBinding().to(PromotionResourceProvider.class);
        resourcesProviderBinder.addBinding().to(LicenseResourcesProvider.class);
    }
}
