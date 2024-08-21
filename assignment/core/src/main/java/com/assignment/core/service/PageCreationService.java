package com.assignment.core.service;

import org.apache.sling.api.resource.ResourceResolver;

public interface PageCreationService {
    void createPage(String path, String title, String description, String tags, String thumbnail, ResourceResolver resolver) throws Exception;
}