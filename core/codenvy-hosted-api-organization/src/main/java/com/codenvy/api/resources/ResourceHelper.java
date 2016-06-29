package com.codenvy.api.resources;

import com.codenvy.api.resources.model.Resource;
import com.codenvy.api.resources.model.impl.ResourceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sergii Leschenko
 */
public class ResourceHelper {
    public static List<ResourceImpl> groupResources(List<ResourceImpl> allResources) {
        Map<String, ResourceImpl> resources = new HashMap<>();
        for (ResourceImpl resource : allResources) {
            final Resource orDefault = resources.get(resource.getType());
            if (orDefault == null) {
                resources.put(resource.getType(), resource);
            } else {
                resources.put(resource.getType(),
                              new ResourceImpl(resource.getType(), resource.getAmount() + orDefault.getAmount()));
            }
        }
        return new ArrayList<>(resources.values());
    }

    public static List<ResourceImpl> reduceUsedResources(List<ResourceImpl> accessibleResources, List<ResourceImpl> usedResources) {
        final Map<String, ResourceImpl> accessible = accessibleResources.stream()
                                                                        .collect(Collectors.toMap(ResourceImpl::getType,
                                                                                                  resource -> resource));
        for (Resource usedResource : usedResources) {
            accessible.computeIfPresent(usedResource.getType(),
                                        (s, resource) -> new ResourceImpl(resource.getType(),
                                                                          resource.getAmount() + usedResource.getAmount()));
        }
        return new ArrayList<>(accessible.values());
    }
}
