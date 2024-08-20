package com.assignment.core.services;

import com.assignment.core.models.PageData;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

/**
 * Service interface for creating pages in AEM.
 * This service is responsible for creating a new page in the AEM repository
 * based on the provided page data.
 */
public interface PageCreationService {

    /**
     * Creates a new page in the AEM repository.
     *

     * @param pageData    the {@link PageData} object containing the data for the new page, such as title, path,
     *                    description, tags, and thumbnail.
     * @throws WCMException if there is an error while creating the page, such as an issue with the provided path
     *                      or a failure to set page properties.
     */

//    void createPage(PageManager pageManager, PageData pageData) throws WCMException;

    void createPage(ResourceResolver resolver, PageData pageData) throws RepositoryException, PersistenceException;
}
