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
    public void createPage(ResourceResolver resolver, PageData pageData) throws RepositoryException {
        Session session = resolver.adaptTo(Session.class);
        if (session == null) {
            throw new RepositoryException("Session could not be retrieved");
        }

        // Generate a base name for the page using the title
        String baseName = pageData.getTitle().toLowerCase().replaceAll("\\s+", "-");

        Resource parentResource = resolver.getResource(pageData.getPath());
        if (parentResource == null) {
            throw new RepositoryException("Parent resource not found at path: " + pageData.getPath());
        }

        Node parentNode = parentResource.adaptTo(Node.class);
        if (parentNode == null) {
            throw new RepositoryException("Parent node could not be adapted from resource");
        }

        // Generate a unique name by checking for existing nodes
        String pageName = generateUniquePageName(parentNode, baseName);
        Node pageNode = parentNode.addNode(pageName, "cq:Page");

        // Create the jcr:content node
        Node contentNode = pageNode.addNode("jcr:content", "cq:PageContent");
        contentNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, "assignment/components/page");
        contentNode.setProperty("jcr:title", pageData.getTitle());
        contentNode.setProperty("jcr:description", pageData.getDescription());
        contentNode.setProperty("cq:tags", pageData.getTags().split(";"));
        contentNode.setProperty("fileReference", pageData.getThumbnail());

        log.info("Page created at path: {}", pageNode.getPath());

        // Save changes to the repository
        session.save();
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
