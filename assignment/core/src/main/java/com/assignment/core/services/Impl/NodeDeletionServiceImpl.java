package com.assignment.core.services.Impl;

import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link NodeDeletionService}.
 * This service is responsible for deleting nodes under a specified root path.
 * It keeps track of the last deleted node and deletes nodes one by one on each invocation.
 */
@Component(service = NodeDeletionService.class, immediate = true)
public class NodeDeletionServiceImpl implements NodeDeletionService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDeletionServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    // Keep track of the last deleted node in a persistent way, e.g., in a config node or a file
    private static String lastDeletedNodePath = "/content/usergenerated/content"; // Starting point

    /**
     * Deletes nodes under the specified root path.
     * This method retrieves child nodes from the root path and deletes them one by one.
     * It updates the path of the last deleted node to ensure sequential deletion.
     *
     * @param rootPath The path of the root node under which child nodes are to be deleted.
     */
    @Override
    public void deleteNodes(String rootPath) {
        Map<String, Object> authenticationMap = new HashMap<>();
        authenticationMap.put(ResourceResolverFactory.SUBSERVICE, "node-deletion-service");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationMap)) {
            Resource rootResource = resourceResolver.getResource(rootPath);

            if (rootResource != null) {
                // Get child resources from the last deleted node path
                Iterator<Resource> childResources = rootResource.listChildren();
                boolean nodeDeleted = false;

                while (childResources.hasNext()) {
                    Resource childResource = childResources.next();
                    if (childResource.getPath().equals(lastDeletedNodePath)) {
                        // Move to the next node
                        continue;
                    }

                    deleteNode(childResource, resourceResolver);
                    lastDeletedNodePath = childResource.getPath(); // Update last deleted node path
                    nodeDeleted = true;
                    break; // Exit after deleting one node
                }

                if (nodeDeleted) {
                    resourceResolver.commit();
                }
            } else {
                LOG.warn("Root node not found: {}", rootPath);
            }
        } catch (PersistenceException e) {
            LOG.error("Failed to commit changes after deleting nodes under {}", rootPath, e);
        } catch (LoginException e) {
            LOG.error("Login error while accessing {}", rootPath, e);
        }
    }

    /**
     * Deletes a specific node.
     *
     * @param resource The node to be deleted.
     * @param resourceResolver The {@link ResourceResolver} used to perform the deletion.
     */
    private void deleteNode(Resource resource, ResourceResolver resourceResolver) {
        try {
            resourceResolver.delete(resource);
            LOG.info("Deleted node: {}", resource.getPath());
        } catch (PersistenceException e) {
            LOG.error("Failed to delete node: {}", resource.getPath(), e);
        }
    }
}
