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
package com.codenvy.api.resources;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.rest.shared.dto.ServiceError;

/**
 * Should be thrown when user doesn't have enough number of resources to perform operation
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public class NotEnoughResourcesException extends ConflictException {//TODO BadRequest vs Conflict
    public NotEnoughResourcesException(String message) {
        super(message);
    }

    public NotEnoughResourcesException(ServiceError serviceError) {
        super(serviceError);
    }
}
