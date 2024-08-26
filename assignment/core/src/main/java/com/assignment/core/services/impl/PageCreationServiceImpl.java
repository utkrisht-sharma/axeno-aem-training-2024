package com.assignment.core.services.impl;

import com.assignment.core.models.PageData;
import com.assignment.core.services.PageCreationService;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link PageCreationService} interface for creating pages in AEM.
 * This service handles the creation of a new page in the repository and sets properties
 * such as description, tags, and thumbnail.
 */
@Component(service = PageCreationService.class, immediate = true)
public class PageCreationServiceImpl implements PageCreationService {

    private static final Logger log = LoggerFactory.getLogger(PageCreationServiceImpl.class);

    @Override
    public void createPage(ResourceResolver resolver, PageData pageData) throws PersistenceException {
        // Generate a base name for the page using the title
        String baseName = pageData.getTitle().toLowerCase().replaceAll("\\s+", "-");

        Resource parentResource = resolver.getResource(pageData.getPath());
        if (parentResource == null) {
            log.error("Parent resource not found at path: {}", pageData.getPath());
            return;
        }

        // Generate a unique name by checking for existing resources
        String pageName = generateUniquePageName(parentResource, baseName);
        Map<String, Object> pageProperties = new HashMap<>();
        pageProperties.put("jcr:primaryType", "cq:Page");

        // Create the page resource
        Resource pageResource = resolver.create(parentResource, pageName, pageProperties);

        // Create the jcr:content node
        Map<String, Object> contentProperties = new HashMap<>();
        contentProperties.put("jcr:primaryType", "cq:PageContent");
        contentProperties.put("sling:resourceType", "assignment/components/page");
        contentProperties.put("jcr:title", pageData.getTitle());

        // Add optional properties
        if (pageData.getDescription() != null && !pageData.getDescription().isEmpty()) {
            contentProperties.put("jcr:description", pageData.getDescription());
        } else {
            log.info("Description is missing for page with Title: {}", pageData.getTitle());
        }

        if (pageData.getTags() != null && !pageData.getTags().isEmpty()) {
            contentProperties.put("cq:tags", pageData.getTags().split(";"));
        } else {
            log.info("Tags are missing for page with Title: {}", pageData.getTitle());
        }

        if (pageData.getThumbnail() != null && !pageData.getThumbnail().isEmpty()) {
            Map<String, Object> imageProperties = new HashMap<>();
            imageProperties.put("jcr:primaryType", "nt:unstructured");
            imageProperties.put("fileReference", pageData.getThumbnail());
            resolver.create(pageResource, "image", imageProperties);
        } else {
            log.info("Thumbnail is missing for page with Title: {}", pageData.getTitle());
        }

        resolver.create(pageResource, "jcr:content", contentProperties);

        // Save the session immediately after page creation
        try {
            resolver.commit();
            log.info("Page created and session saved at path: {}", pageResource.getPath());
        } catch (PersistenceException e) {
            log.error("Error saving session for page at {}: {}", pageResource.getPath(), e.getMessage());
            throw e;  // Rethrow the exception to ensure it's handled by the calling code if needed
        }
    }

    private String generateUniquePageName(Resource parentResource, String baseName) {
        String uniqueName = baseName;
        int counter = 0;

        // Check if a resource with the same name already exists and generate a new one if necessary
        while (parentResource.getChild(uniqueName) != null) {
            counter++;
            uniqueName = baseName + "-" + counter;
        }

        return uniqueName;
    }
}
