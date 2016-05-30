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

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.shared.dto.ServiceError;

/**
 * @author gazarenkov
 */
//TODO Mb it would be better to extend other exception like BadRequest or Conflict
public class NotEnoughResourcesException extends ServerException {

    public NotEnoughResourcesException(String message) {
        super(message);
    }

    public NotEnoughResourcesException(ServiceError serviceError) {
        super(serviceError);
    }

    public NotEnoughResourcesException(Throwable cause) {
        super(cause);
    }

    public NotEnoughResourcesException(String message, Throwable cause) {
        super(message, cause);
    }
}
