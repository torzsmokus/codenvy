package com.codenvy.api.resources.model;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public interface OrganizationResources {
    List<Resource> getOwn();

    List<Resource> getCommon();
}
