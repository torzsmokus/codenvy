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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

// TODO document

/**
 * @author Yevhenii Voevodin
 */
public final class LdapCloser {

    private static final Logger LOG = LoggerFactory.getLogger(LdapCloser.class);

    public static void close(DirContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException x) {
                LOG.error(x.getMessage(), x);
            }
        }
    }

    public static void close(NamingEnumeration<?> enumeration) {
        if (enumeration != null) {
            try {

                enumeration.close();
            } catch (NamingException x) {
                LOG.error(x.getMessage(), x);
            }
        }
    }
}
