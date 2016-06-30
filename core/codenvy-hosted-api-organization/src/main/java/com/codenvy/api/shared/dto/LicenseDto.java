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
package com.codenvy.api.shared.dto;

import com.codenvy.api.license.model.License;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
@DTO
public interface LicenseDto extends License {
    @Override
    String getOwner();

    void setOwner(String owner);

    LicenseDto withOwner(String owner);

    @Override
    List<ResourceDto> getResources();

    void setResources(List<ResourceDto> resources);

    LicenseDto withResources(List<ResourceDto> resources);
}
