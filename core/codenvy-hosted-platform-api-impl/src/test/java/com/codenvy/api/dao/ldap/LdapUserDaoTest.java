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

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

@Listeners({MockitoTestNGListener.class})
public class LdapUserDaoTest extends BaseTest {

    LdapUserDao               userDao;
    InitialLdapContextFactory factory;
    UserAttributesMapper      mapper;
    UserImpl[]                users;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = spy(new InitialLdapContextFactory(embeddedLdapServer.getUrl(),
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null));
        mapper = spy(new UserAttributesMapper());
        userDao = new LdapUserDao(factory,
                                  "dc=codenvy;dc=com",
                                  "uid",
                                  "cn",
                                  mapper,
                                  new EventService());

        users = new UserImpl[] {
                new UserImpl("1", "user1@mail.com", "user1", "secret", singletonList("user1@mail.com")),
                new UserImpl("2", "user2@mail.com", "user2", "secret", singletonList("user2@mail.com")),
                new UserImpl("3", "user3@mail.com", "user3", "secret", singletonList("user3@mail.com"))
        };
        for (UserImpl user : users) {
            userDao.create(user);
        }
    }

    @Test
    public void shouldAuthenticateUserUsingAliases() throws Exception {
        assertEquals(userDao.authenticate(users[0].getAliases().get(0), users[0].getPassword()), users[0].getId());
    }

    @Test
    public void shouldAuthenticateUserUsingName() throws Exception {
        assertEquals(userDao.authenticate(users[0].getName(), users[0].getPassword()), users[0].getId());
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldNotAuthenticateUserWithWrongPassword() throws Exception {
        userDao.authenticate(users[0].getName(), "invalid");
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldNotAuthenticateUserWithEmptyAlias() throws Exception {
        userDao.authenticate("", "valid");
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldNotAuthenticateUserWithEmptyPassword() throws Exception {
        userDao.authenticate(users[0].getName(), "");
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldNotAuthenticateUserWithNullAlias() throws Exception {
        userDao.authenticate(null, "valid");
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldNotAuthenticateUserWithNullPassword() throws Exception {
        userDao.authenticate(users[0].getName(), null);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void shouldThrowNotFoundExceptionWhenAuthenticatingNotExistingUser() throws Exception {
        userDao.authenticate("not_found", "secret");
    }

    @Test(expectedExceptions = ServerException.class)
    public void shouldWrapAnyNamingExceptionWithServerExceptionWhenAuthenticatingUser() throws Exception {
        when(factory.createContext()).thenThrow(new NamingException("message"));

        userDao.authenticate(users[0].getName(), users[0].getPassword());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        final UserImpl newUser = new UserImpl("user123", "user123@mail.com", "user123_name", "password", null);

        userDao.create(newUser);

        final UserImpl result = userDao.getById("user123");
        assertEquals(result.getId(), newUser.getId());
        assertEquals(result.getName(), newUser.getName());
        assertEquals(result.getEmail(), newUser.getEmail());
        assertEquals(result.getAliases(), singletonList(newUser.getEmail()));
        assertNull(result.getPassword());
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "Unable create new user .*. User already exists")
    public void shouldThrowConflictExceptionWhenCreatingUserWithReservedId() throws Exception {
        final UserImpl newUser = new UserImpl(users[0]);
        newUser.setEmail("example@mail.com");
        newUser.setName("new_name");
        newUser.setAliases(singletonList("example@mail.com"));
        newUser.setPassword("new password");

        userDao.create(newUser);
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "User with alias .* already exists")
    public void shouldThrowConflictExceptionWhenCreatingUserWithReservedAlias() throws Exception {
        final UserImpl copy = new UserImpl(users[0]);
        copy.setName("new_name");
        copy.setEmail("example@mail.com");
        copy.setPassword("new_secret");
        copy.getAliases().add("example@mail.com");

        userDao.create(copy);
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "User with name .* already exists")
    public void shouldThrowConflictExceptionWhenCreatingUserWithReservedName() throws Exception {
        final UserImpl copy = new UserImpl(users[0]);
        copy.setEmail("example@mail.com");
        copy.setAliases(singletonList("example@mail.com"));
        copy.setPassword("new password");

        userDao.create(copy);
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "Unable create new user .*. User already exists")
    public void shouldWrapAnyNameAlreadyBoundExceptionWithConflictExceptionWhenCreatingUser() throws Exception {
        when(factory.createContext()).thenThrow(new NameAlreadyBoundException());

        userDao.create(users[0]);
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "User with email .* already exists")
    public void shouldThrowConflictExceptionWhenCreatingTwoUsersWithSameEmails() throws Exception {
        userDao.create(new UserImpl("id_1", "example@mail.com", "name1", "password", null));
        userDao.create(new UserImpl("id_2", "example@mail.com", "name2", "password", null));
    }

    @Test(expectedExceptions = ServerException.class)
    public void shouldWrapAnyNamingExceptionWithServerExceptionWhenCreatingUser() throws Exception {
        when(factory.createContext()).thenThrow(new NamingException());

        userDao.create(users[0]);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        final UserImpl copy = new UserImpl(users[0]);
        copy.setEmail("example@mail.com");
        copy.setName("new_name");
        copy.setPassword("new_secret");
        copy.setAliases(singletonList("example@mail.com"));

        userDao.update(copy);

        final UserImpl updated = userDao.getById(copy.getId());
        assertEquals(updated.getId(), copy.getId());
        assertEquals(updated.getName(), copy.getName());
        assertEquals(updated.getEmail(), copy.getEmail());
        assertEquals(updated.getPassword(), null);
        assertEquals(updated.getAliases(), copy.getAliases());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUpdatingNotExistingUser() throws Exception {
        userDao.update(new UserImpl("non-existing-id"));
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "Unable update user .*, alias .* is already in use")
    public void shouldThrowConflictExceptionWhenUpdatingUserWithAliasWhichIsReserved() throws Exception {
        final UserImpl copy = new UserImpl(users[0]);
        copy.setEmail("example@mail.com");
        copy.setPassword("new_secret");
        final String conflictAlias = users[1].getAliases().get(0);
        copy.getAliases().add("example@mail.com");
        copy.getAliases().add(conflictAlias);

        userDao.update(copy);
    }

    @Test(expectedExceptions = ConflictException.class,
          expectedExceptionsMessageRegExp = "Unable update user '.*', name '.*' already in use")
    public void shouldThrowConflictExceptionWhenUpdatingUserWithNameWhichIsReserved() throws Exception {
        final UserImpl user = new UserImpl(users[0]);
        user.setEmail("new-email@mail.com");
        user.setName(users[1].getName());
        user.setPassword("new secret");

        userDao.update(user);
    }

    @Test(expectedExceptions = ServerException.class)
    public void shouldWrapAnyNamingExceptionWithServerExceptionWhenUpdatingUser() throws Exception {
        when(factory.createContext()).thenThrow(new NamingException());

        userDao.update(users[0]);
    }

    @Test
    public void shouldBeAbleToGetUserByAlias() throws Exception {
        final UserImpl user = userDao.getByAlias(users[2].getAliases().get(0));

        assertEquals(user.getId(), users[2].getId());
        assertEquals(user.getEmail(), users[2].getEmail());
        assertEquals(user.getName(), users[2].getName());
        assertEquals(user.getPassword(), null);
        assertEquals(user.getAliases(), users[2].getAliases());
    }

    @Test
    public void shouldBeAbleToGetUserByName() throws Exception {
        final UserImpl user = userDao.getByName(users[2].getName());

        assertEquals(user.getId(), users[2].getId());
        assertEquals(user.getEmail(), users[2].getEmail());
        assertEquals(user.getName(), users[2].getName());
        assertEquals(user.getPassword(), null);
        assertEquals(user.getAliases(), users[2].getAliases());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUserWithGivenAliasDoesNotExist() throws Exception {
        userDao.getByAlias("invalid");
    }

    @Test(expectedExceptions = ServerException.class)
    public void shouldWrapAnyNamingExceptionWithServerExceptionWhenGettingUserByAlias() throws Exception {
        when(factory.createContext()).thenThrow(new NamingException());

        userDao.getByAlias("valid");
    }

    @Test
    public void shouldBeAbleToGetUserById() throws Exception {
        final UserImpl user = userDao.getById(users[1].getId());

        assertEquals(user.getId(), users[1].getId());
        assertEquals(user.getEmail(), users[1].getEmail());
        assertEquals(user.getName(), users[1].getName());
        assertEquals(user.getPassword(), null);
        assertEquals(user.getAliases(), users[1].getAliases());
    }

    @Test
    public void shouldRenameEntityWhenItIsNotFoundWithNewDn() throws Exception {
        final Attributes attributes = mapper.toAttributes(new UserImpl("user123", "user123@mail.com", "user123", "password", null));
        InitialLdapContext context = factory.createContext();
        context.createSubcontext("cn=user123,dc=codenvy;dc=com", attributes);

        userDao.getById("user123");

        try {
            context.getAttributes("cn=user123,dc=codenvy;dc=com");
            fail("Should rename entity");
        } catch (NameNotFoundException ignored) {
            //it okay
        }
        assertNotNull(context.getAttributes("uid=user123,dc=codenvy;dc=com"));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUserWithGivenIdDoesNotExist() throws Exception {
        userDao.getById("invalid");
    }

    @Test(expectedExceptions = ServerException.class)
    public void shouldWrapAnyNamingExceptionWithServerExceptionWhenGettingUserById() throws Exception {
        when(factory.createContext()).thenThrow(new NamingException());

        userDao.getById("valid");
    }
}
