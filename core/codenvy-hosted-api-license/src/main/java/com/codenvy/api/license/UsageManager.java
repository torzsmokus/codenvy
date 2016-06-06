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

import com.codenvy.api.license.data.LicenseData;
import com.codenvy.api.license.model.LicenseType;
import com.codenvy.api.license.model.Resource;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gazarenkov
 */
@Singleton
public class UsageManager {
    private final Map<String, LicenseType> licenseTypes;
    private final Map<String, License>     runtimeLicenses;
    private final SessionRegistry          sessionRegistry;
    private final LicenseDao               licenseDao;

    @Inject
    public UsageManager(SessionRegistry sessionRegistry, LicenseDao licenseDao, Set<LicenseType> types) {
        this.licenseDao = licenseDao;
        this.sessionRegistry = sessionRegistry;
        this.licenseTypes = new HashMap<>();
        for (LicenseType type : types) {
            licenseTypes.put(type.getId(), type);
        }
        this.runtimeLicenses = new HashMap<>();
    }

    /**
     * called by resource consumer (machine service)
     *
     * @param identity
     * @param resources
     * @return
     */
    public ActiveSession start(String user, Identity identity, Set<Resource> resources) throws NotEnoughResourcesException,
                                                                                               ServerException, NotFoundException {
        return sessionRegistry.add(identity, resources);
    }

    /**
     * called by resource consumer (machine service)
     *
     * @param sessionId
     */
    public void stop(String sessionId) throws NotFoundException {
        ActiveSession session = sessionRegistry.getActive(sessionId);
        session.getLicense().removeSession(sessionId);
        sessionRegistry.remove(sessionId);
    }

    public void check(String licenseType) throws NotFoundException {
        LicenseType type = this.licenseTypes.get(licenseType);
        if (type == null) {
            throw new NotFoundException("No license type found  " + licenseType);
        }

        List<String> licensesToPassivate = new ArrayList<>();
        for (License license : runtimeLicenses.values()) {
            if (!license.check()) {
                licensesToPassivate.add(license.getId());
            }
        }

        // remove
        // todo need storing it to history
        for (String licenseId : licensesToPassivate) {
            for (ActiveSession session : runtimeLicenses.get(licenseId).getSessions()) {
                sessionRegistry.remove(session.getId());
            }

            runtimeLicenses.remove(licenseId);
            licenseDao.delete(licenseId);
        }
    }
}
