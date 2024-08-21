package com.assignment.core.services.Impl;

import com.assignment.core.services.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import java.util.List;

/**
 * Implementation of the PageCreationService interface for creating pages in a content repository.
 * This service implementation handles the creation of multiple pages from a list of CSV data.
 * It utilizes the  PageManager for page creation and  ResourceResolver for
 * repository interactions.
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
            createPage(pageManager, csvData);
        }
    }

    /**
     * Creates a single page based on the provided CSV data.
     */
    @Override
    public void createPage(PageManager pageManager, String[] csvData) {
        try {
            String title = csvData[0].trim();
            String path = csvData[1].trim();
            String description = csvData[2].trim();
            String[] tags = csvData[3].trim().split("\\|");
            String thumbnail = csvData[4].trim();
            String pageName = generateValidName(title);
            Page page = pageManager.create(path, pageName, "/conf/global/settings/wcm/templates/page", title);

            if (page != null) {
                LOG.info("Page created successfully: {}/{}", path, title);
                setPageProperties(page, description, tags, thumbnail);
            } else {
                LOG.warn("Page creation failed for title: {}", title);
            }
        } catch (Exception e) {
            LOG.error("Error creating page", e);
        }
    }

    /**
     * Sets properties for the created page.
     */
    private void setPageProperties(Page page, String description, String[] tags, String thumbnail) {
        try {
            Node pageContentNode = page.getContentResource().adaptTo(Node.class);
            if (pageContentNode != null) {
                LOG.info("Setting properties for page: {}", page.getTitle());
                pageContentNode.setProperty("jcr:description", description);
                pageContentNode.setProperty("cq:tags", tags);
                pageContentNode.setProperty("cq:thumbnail", thumbnail);
            } else {
                LOG.warn("Page content node is null for page: {}", page.getTitle());
            }
        } catch (Exception e) {
            LOG.error("Error setting page properties", e);
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
