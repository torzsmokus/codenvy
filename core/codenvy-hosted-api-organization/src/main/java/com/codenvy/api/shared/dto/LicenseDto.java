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
import com.codenvy.api.resources.model.Resource;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
@DTO
public interface LicenseDto extends License {
    @Override
    String getId();

    void setId(String id);

    LicenseDto withId(String id);

    @Override
    String getOwner();

    void setOwner(String owner);

    LicenseDto withOwner(String owner);

@Override
String getType();

void setType(String type);

LicenseDto withType(String type);

    @Override
    long getStartTime();

    void setStartTime(long startTime);

    LicenseDto withStartTime(long startTime);

    @Override
    long getEndTime();

    void setEndTime(long endTime);

    LicenseDto withEndTime(long endTime);

    @Override
    List<ResourceDto> getResources();

    void setResources(List<ResourceDto> resources);

    LicenseDto withResources(List<ResourceDto> resources);

    @Override
    Map<String, String> getAttributes();

    void setAttributes(Map<String, String> attributes);

    LicenseDto withAttributes(Map<String, String> attributes);
}
