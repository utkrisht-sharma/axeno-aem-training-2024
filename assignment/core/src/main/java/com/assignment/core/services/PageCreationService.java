package com.assignment.core.services;

import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;
import com.day.cq.wcm.api.WCMException;

/**
 * Service interface for creating pages.
 */
public interface PageCreationService {

    /**
     * Creates a page and sets its properties.
     *
     * @throws WCMException       if a page creation error occurs.
     * @throws PersistenceException if a persistence error occurs.
     */
    void createPage(PageManager pageManager, ResourceResolver resourceResolver, String path, String pageName, String title, String description, String tags, String thumbnail)
            throws WCMException, PersistenceException;
}
