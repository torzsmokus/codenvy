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
package com.codenvy.api.dao.ldap;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.commons.test.tck.TckModule;
import org.eclipse.che.commons.test.tck.TckRepository;

public class LdapTckModule implements TckModule {

    @Override
    public void configure(Binder binder) {

        binder.bind(UserAttributesMapper.class).toInstance(new UserAttributesMapper());
        binder.bind(new TypeLiteral<TckRepository<UserImpl>>() {}).to(UserTckRepository.class);

        binder.bind(UserDao.class).toInstance(new UserDao() {
            @Override
            public String authenticate(String emailOrAliasOrName, String password)
                    throws UnauthorizedException, ServerException {
                return null;
            }

            @Override
            public void create(UserImpl user) throws ConflictException, ServerException {

            }

            @Override
            public void update(UserImpl user) throws NotFoundException, ServerException, ConflictException {

            }

            @Override
            public void remove(String id) throws ServerException, ConflictException {

            }

            @Override
            public UserImpl getByAlias(String alias) throws NotFoundException, ServerException {
                return null;
            }

            @Override
            public UserImpl getById(String id) throws NotFoundException, ServerException {
                return null;
            }

            @Override
            public UserImpl getByName(String name) throws NotFoundException, ServerException {
                return null;
            }

            @Override
            public UserImpl getByEmail(String email) throws NotFoundException, ServerException {
                return null;
            }
        });
    }
}
