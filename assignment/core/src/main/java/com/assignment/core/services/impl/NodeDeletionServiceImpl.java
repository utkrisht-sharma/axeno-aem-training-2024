package com.assignment.core.services.impl;

import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Implementation of NodeDeletionService for deleting nodes under a specific path.
 */
@Component(service = NodeDeletionService.class, immediate = true)
public class NodeDeletionServiceImpl implements NodeDeletionService {
    private static final Logger log = LoggerFactory.getLogger(NodeDeletionService.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Deletes all child nodes under the given path.
     */
    @Override
    public void deleteNode(String path) {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            Resource rootResource = resourceResolver.getResource(path);
            if (rootResource != null) {
                for (Resource child : rootResource.getChildren()) {
                    try {
                        resourceResolver.delete(child);
                        log.info("Node Deleted At Path: {}", child.getPath());
                    } catch (PersistenceException e) {
                        log.error("Unable to delete Node At Path: {}", child.getPath(), e);
                    }
                }
                resourceResolver.commit();
                log.info("Deletion committed successfully.");
            } else {
                log.warn("Resource not found at path: {}", path);
            }
        } catch (PersistenceException e) {
            log.error("PersistenceException occurred while deleting nodes under path: {}", path, e);
        } catch (LoginException e) {
            log.error("LoginException occurred while obtaining service resource resolver.", e);
        } catch (Exception e) {
            log.error("Unexpected Error occurred while deleting nodes under path: {}", path, e);
        }
    }

    /**
     * Obtains a service resource resolver using the nodeDeletionService service user.
     */
    private ResourceResolver getServiceResourceResolver() throws LoginException {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "nodeDeletionService");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

}

