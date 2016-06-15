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
package com.codenvy.api.account;

import com.codenvy.api.account.impl.AccountImpl;
import com.codenvy.api.organization.model.Organization;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.che.commons.lang.NameGenerator;

/**
 * Creates organization account after its creation
 *
 * @author Sergii Leschenko
 */
//TODO Maybe implement creating personal account if user request it but doesn't have
//TODO It will be usefull for existing users or if user was created by ldap manually
public class OrganizationAccountProvider implements MethodInterceptor {
    AccountDao accountDao;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final Organization organization = (Organization)methodInvocation.getArguments()[0];
        final Object result = methodInvocation.proceed();

        accountDao.create(new AccountImpl(NameGenerator.generate("account", 16), organization.getId(), Account.Type.ORGANIZATIONAL));

        return result;
    }
}
