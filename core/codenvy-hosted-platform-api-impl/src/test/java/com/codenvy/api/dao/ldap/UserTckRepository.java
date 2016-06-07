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

import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.commons.test.tck.TckRepository;
import org.testng.ITestContext;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import java.util.Collection;

/**
 * @author Yevhenii Voevodin
 */
public class UserTckRepository implements TckRepository<UserImpl> {

    @Inject
    private ITestContext testContext;

    private final UserAttributesMapper      mapper;
    private final InitialLdapContextFactory ctxFactory;

    public UserTckRepository(UserAttributesMapper mapper) {
        this.mapper = mapper;
        this.ctxFactory = new InitialLdapContextFactory(testContext.getAttribute("ldap_server_url").toString(),
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null);
    }

    @Override
    public void createAll(Collection<? extends UserImpl> users) {
        InitialLdapContext context = null;
        DirContext newContext = null;
        try {
            context = ctxFactory.createContext();

            for (UserImpl user : users) {
                try {
                    final Attributes attributes = mapper.toAttributes(user);
                    newContext = context.createSubcontext("dc=codenvy;dc=com;uid=" + user.getId(), attributes);
                } catch (NamingException x) {
                    throw new RuntimeException(x.getMessage(), x);
                } finally {
                    context.close();
                }
            }
        } catch (NamingException x) {
            throw new RuntimeException(x.getMessage(), x);
        } finally {
            LdapCloser.close(context);
            LdapCloser.close(newContext);
        }
    }

    @Override
    public void removeAll() {

    }
}
