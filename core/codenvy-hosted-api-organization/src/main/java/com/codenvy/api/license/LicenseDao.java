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

import com.codenvy.api.license.model.License;
import com.codenvy.api.license.model.impl.LicenseImpl;
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Defines data access object contract for {@link License}.
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class LicenseDao {
    private final HashMap<String, LicenseImpl> licenses;
    private final LocalStorage                 licenseStorage;

    @Inject
    public LicenseDao(LocalStorageFactory storageFactory) throws IOException {
        this.licenseStorage = storageFactory.create("licenses");
        this.licenses = new HashMap<>();
    }


    @PostConstruct
    public void load() {
        licenses.putAll(licenseStorage.loadMap(new TypeToken<Map<String, LicenseImpl>>() {}));
    }

    @PreDestroy
    public void save() throws IOException {
        licenseStorage.store(licenses);
    }

    /**
     * Adds license
     */
    public void create(LicenseImpl license) throws ConflictException {
        if (licenses.containsKey(license.getId())) {
            throw new ConflictException(String.format("License with id '%s' already exists", license.getId()));
        }
        licenses.put(license.getId(), license);
    }

    /**
     * Deletes license by id
     *
     * @param id
     *         id of license
     */
    public void delete(String id) throws NotFoundException {
        if (licenses.remove(id) == null) {
            throw new NotFoundException(String.format("License with id '%s' was not found", id));
        }
    }

    /**
     * Returns license by id
     *
     * @param id
     *         the id
     * @return license data
     */
    public LicenseImpl get(String id) throws NotFoundException {
        final LicenseImpl license = licenses.get(id);
        if (license == null) {
            throw new NotFoundException(String.format("License with id '%s' was not found", id));
        }
        return license;
    }

    /**
     * Returns license by owner
     *
     * @param owner
     *         id of account owner
     * @return license data
     */
    public LicenseImpl getByOwner(String owner) throws NotFoundException {
        final Optional<LicenseImpl> optLicense = licenses.values()
                                                         .stream()
                                                         .filter(license -> license.getOwner().equals(owner))
                                                         .findAny();
        if (!optLicense.isPresent()) {
            throw new NotFoundException(String.format("License with owner '%s' was not found", owner));
        }

        return optLicense.get();
    }
}
