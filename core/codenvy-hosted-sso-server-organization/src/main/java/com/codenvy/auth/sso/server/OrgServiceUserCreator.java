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
package com.codenvy.auth.sso.server;


import com.codenvy.auth.sso.server.organization.UserCreator;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.user.Profile;
import org.eclipse.che.api.core.model.user.User;
import org.eclipse.che.api.user.server.PreferencesManager;
import org.eclipse.che.api.user.server.ProfileManager;
import org.eclipse.che.api.user.server.UserManager;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.commons.lang.NameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergii Kabashniuk
 */
public class OrgServiceUserCreator implements UserCreator {
    private static final Logger LOG = LoggerFactory.getLogger(OrgServiceUserCreator.class);

    @Inject
    private UserManager userManager;

    @Inject
    private ProfileManager profileManager;

    @Inject
    private PreferencesManager preferencesManager;

    @Inject
    @Named("user.self.creation.allowed")
    private boolean userSelfCreationAllowed;

    @Override
    public User createUser(String email, String userName, String firstName, String lastName) throws IOException {
        //TODO check this method should only call if user is not exists.
        try {
            return userManager.getByEmail(email);
        } catch (NotFoundException e) {
            if (!userSelfCreationAllowed) {
                throw new IOException("Currently only admins can create accounts. Please contact our Admin Team for further info.");
            }

            try {
                final User user = userManager.create(new UserImpl(null, email, userName), false);

                // Updating preferences
                final Map<String, String> preferences = new HashMap<>();
                preferences.put("resetPassword", "true");
                preferencesManager.update(user.getId(), preferences);

                // Updating profile
                final Profile profile = profileManager.getById(user.getId());
                profile.getAttributes().put("firstName", firstName);
                profile.getAttributes().put("lastName", lastName);
                profileManager.update(profile);

                return user;
            } catch (ConflictException | ServerException | NotFoundException e1) {
                throw new IOException(e1.getLocalizedMessage(), e1);
            }
        } catch (ServerException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }

    }

    @Override
    public User createTemporary() throws IOException {

        try {
            String testName;
            while (true) {
                testName = NameGenerator.generate("AnonymousUser_", 6);
                try {
                    userManager.getByName(testName);
                } catch (NotFoundException e) {
                    break;
                } catch (ApiException e) {
                    throw new IOException(e.getLocalizedMessage(), e);
                }
            }


            final String anonymousUser = testName;

            final User user = userManager.create(new UserImpl(null, anonymousUser, anonymousUser), true);

            final Map<String, String> preferences = new HashMap<>();
            preferences.put("temporary", String.valueOf(true));
            preferencesManager.update(user.getId(), preferences);

            LOG.info("Temporary user {} created", anonymousUser);
            return user;
        } catch (ApiException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }
    }
}
