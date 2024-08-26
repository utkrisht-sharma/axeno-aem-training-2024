package com.assignment.core.service;

import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

public interface PageCreationService {
    void createPage(String path, String title, String description, String tags, String thumbnail, ResourceResolver resolver) throws PersistenceException, WCMException;
}