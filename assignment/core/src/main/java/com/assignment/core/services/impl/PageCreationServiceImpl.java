package com.assignment.core.services.impl;

import com.assignment.core.models.PageData;
import com.assignment.core.services.PageCreationService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Implementation of the {@link PageCreationService} interface for creating pages in AEM.
 * This service handles the creation of a new page in the repository and sets properties
 * such as description, tags, and thumbnail.
 */
@Component(service = PageCreationService.class, immediate = true)
public class PageCreationServiceImpl implements PageCreationService {

    private static final Logger log = LoggerFactory.getLogger(PageCreationServiceImpl.class);

    @Override
    public void createPage(ResourceResolver resolver, PageData pageData) {
        Session session = resolver.adaptTo(Session.class);
        if (session == null) {
            log.error("Session could not be retrieved");
            return;
        }

        try {
            // Generate a base name for the page using the title
            String baseName = pageData.getTitle().toLowerCase().replaceAll("\\s+", "-");

            Resource parentResource = resolver.getResource(pageData.getPath());
            if (parentResource == null) {
                log.error("Parent resource not found at path: {}", pageData.getPath());
                return;
            }

            Node parentNode = parentResource.adaptTo(Node.class);
            if (parentNode == null) {
                log.error("Parent node could not be adapted from resource");
                return;
            }

            // Generate a unique name by checking for existing nodes
            String pageName = generateUniquePageName(parentNode, baseName);
            Node pageNode = parentNode.addNode(pageName, "cq:Page");

            // Create the jcr:content node
            Node contentNode = pageNode.addNode("jcr:content", "cq:PageContent");
            contentNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, "assignment/components/page");
            contentNode.setProperty("jcr:title", pageData.getTitle());

            // Check and set the description if present
            if (pageData.getDescription() != null && !pageData.getDescription().isEmpty()) {
                contentNode.setProperty("jcr:description", pageData.getDescription());
            } else {
                log.info("Description is missing for page with Title: {}", pageData.getTitle());
            }

            // Check and set the tags if present
            if (pageData.getTags() != null && !pageData.getTags().isEmpty()) {
                contentNode.setProperty("cq:tags", pageData.getTags().split(";"));
            } else {
                log.info("Tags are missing for page with Title: {}", pageData.getTitle());
            }

            // Check and set the thumbnail if present
            if (pageData.getThumbnail() != null && !pageData.getThumbnail().isEmpty()) {
                Node imageNode = contentNode.addNode("image", "nt:unstructured");
                imageNode.setProperty("fileReference", pageData.getThumbnail());
            } else {
                log.info("Thumbnail is missing for page with Title: {}", pageData.getTitle());
            }

            log.info("Page created at path: {}", pageNode.getPath());

        } catch (RepositoryException e) {
            log.error("Error creating page for Title: {}. Error: {}", pageData.getTitle(), e.getMessage(), e);
        }
    }

    @Override
    public void saveSession(ResourceResolver resolver) {
        Session session = resolver.adaptTo(Session.class);
        if (session != null) {
            try {
                session.save();
                log.info("Session saved successfully.");
            } catch (RepositoryException e) {
                log.error("Error saving session: {}", e.getMessage(), e);
            }
        }
    }

    private String generateUniquePageName(Node parentNode, String baseName) throws RepositoryException {
        String uniqueName = baseName;
        int counter = 0;

        // Check if a node with the same name already exists and generate a new one if necessary
        while (parentNode.hasNode(uniqueName)) {
            counter++;
            uniqueName = baseName + "-" + counter;
        }

        return uniqueName;
    }
}



