package com.assignment.core.services.Impl;

import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation of the NodeDeletionService interface that deletes nodes under the specified root path.
 */
@Component(service = NodeDeletionService.class)
public class NodeDeletionServiceImpl implements Runnable, NodeDeletionService {

    private static final Logger log = LoggerFactory.getLogger(NodeDeletionServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String rootPath;

    @Override
    public void run() {
        deleteNodes();
    }

    @Override
    public void deleteNodes() {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            Resource rootResource = resourceResolver.getResource(rootPath);
            if (rootResource != null) {
                for (Resource child : rootResource.getChildren()) {
                    try {
                        resourceResolver.delete(child);
                        log.info("Marked for deletion: {}", child.getPath());
                    } catch (PersistenceException e) {
                        log.error("Failed to mark node for deletion: {}", child.getPath(), e);
                    }
                }
                resourceResolver.commit();
                log.info("Deletion committed successfully.");
            } else {
                log.warn("Root path not found: {}", rootPath);
            }
        } catch (PersistenceException e) {
            log.error("Persistence error occurred while deleting nodes under path: {}", rootPath, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting nodes under path: {}", rootPath, e);
        }
    }

    private ResourceResolver getServiceResourceResolver() throws LoginException {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "nodeDeletionService");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }
}
