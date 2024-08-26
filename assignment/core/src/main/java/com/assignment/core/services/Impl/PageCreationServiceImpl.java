package com.assignment.core.services.Impl;

import com.assignment.core.services.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Implementation of the PageCreationService interface for creating pages in a content repository.
 */
@Component(service = PageCreationService.class)
public class PageCreationServiceImpl implements PageCreationService {

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationServiceImpl.class);

    /**
     * Creates multiple pages based on the provided CSV data.
     */
    @Override
    public void createPages(List<String[]> csvDataList, ResourceResolver resolver) {
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.error("PageManager is null");
            return;
        }
        for (String[] csvData : csvDataList) {
            createPage(pageManager, csvData, resolver);
        }
    }

    /**
     * Creates a single page based on the provided CSV data.
     */
    @Override
    public void createPage(PageManager pageManager, String[] csvData, ResourceResolver resolver) {
        try {
            String title = csvData[0].trim();
            String path = csvData[1].trim();
            String description = csvData[2].trim();
            String[] tags = csvData[3].trim().split("\\|");
            String thumbnail = csvData[4].trim();
            String pageName = generateValidName(title);
            String templatePath="/conf/global/settings/wcm/templates/page";
            Page page = pageManager.create(path, pageName, templatePath, title);

            if (page != null) {
                LOG.info("Page created successfully: {}/{}", path, title);
                setPageProperties(page, description, tags, thumbnail, resolver);
            } else {
                LOG.warn("Page creation failed for title: {}", title);
            }
        }catch(WCMException e){
            LOG.error("WCMException occurred while creating page: {}",e);
        }catch (Exception e) {
            LOG.error("Unexpected error while creating page: {}", e.getMessage(), e);
        }
    }

    /**
     * Sets properties for the created page using the Resource API.
     */
    private void setPageProperties(Page page, String description, String[] tags, String thumbnail, ResourceResolver resolver) {
        Resource pageContentResource = page.getContentResource();
        if (pageContentResource != null) {
            try {
                ModifiableValueMap properties = pageContentResource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    LOG.info("Setting properties for page: {}", page.getTitle());
                    properties.put("jcr:description", description);
                    properties.put("cq:tags", tags);
                    properties.put("cq:thumbnail", thumbnail);
                    resolver.commit();
                } else {
                    LOG.warn("ModifiableValueMap is null for page: {}", page.getTitle());
                }
            } catch (PersistenceException e) {
                LOG.error("Error committing page properties: {}", e.getMessage(), e);
            }
        } else {
            LOG.warn("Page content resource is null for page: {}", page.getTitle());
        }
    }

    /**
     * Generates a valid page name from the given title.
     */
    private String generateValidName(String title) {
        String validName = title.replaceAll("[^\\p{Alnum}]", "-").toLowerCase();
        return validName.isEmpty() ? "default-page" : validName;
    }
}
