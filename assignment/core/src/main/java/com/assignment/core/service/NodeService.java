package com.assignment.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface NodeService {

    void processSaveOrDelete(ResourceResolver resolver, String saveFlag, long matchCount) throws Exception;
}
