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

import com.codenvy.api.license.model.impl.LicenseImpl;
import com.codenvy.api.shared.dto.LicenseDto;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author Sergii Leschenko
 */
@Path("license")
public class LicenseService extends Service {
    private final LicenseDao licenseDao;

    @Inject
    public LicenseService(LicenseDao licenseDao) {
        this.licenseDao = licenseDao;
    }

    @GET
    @Path("{account}")
    public LicenseDto getLicense(@PathParam("account") String account) throws NotFoundException {
        return asDto(licenseDao.getByOwner(account));
    }

    private LicenseDto asDto(LicenseImpl license) {
        return DtoFactory.newDto(LicenseDto.class)
                .withId(license.getId())
//                .withResources(license.getResources())
                .withStartTime(license.getStartTime())
                .withEndTime(license.getEndTime())
                .withAttributes(license.getAttributes())
                .withOwner(license.getOwner());
    }
}
