package com.assignment.core.services.impl;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assignment.core.services.NodeDeletionService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of the NodeDeletionService that deletes nodes under a specified JCR path.
 */
@Component(service = NodeDeletionService.class, immediate = true)
public class NodeDeletionServiceImpl implements NodeDeletionService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDeletionServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Deletes one child node under the specified root path, excluding system nodes like `jcr:content`.
     *
     * @param rootPath The root path under which child nodes will be deleted.
     * @return {@code true} if a node was deleted and more nodes may remain;
     *         {@code false} if no nodes are left to delete.
     */
    @Override
    public boolean deleteNodesUnderPath(String rootPath) {
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(getResourceResolver())) {
            Resource rootResource = resourceResolver.getResource(rootPath);
            if (rootResource != null) {
                Iterator<Resource> children = rootResource.listChildren();
                while (children.hasNext()) {
                    Resource child = children.next();

                    // Skip the deletion of `jcr:content` or other system nodes
                    if (!"jcr:content".equals(child.getName())) {
                        try {
                            resourceResolver.delete(child);
                            resourceResolver.commit();
                            LOG.info("Deleted node: {}", child.getPath());
                            return true; // Node deleted, more nodes may remain
                        } catch (PersistenceException e) {
                            LOG.error("Failed to delete node: {}", child.getPath(), e);
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOG.error("Failed to obtain ResourceResolver", e);
        }
        return false; // No nodes left to delete
    }

    /**
     * Retrieves a {@link ResourceResolver} using the specified subservice.
     *
     * @return A map containing the necessary parameters for obtaining a ResourceResolver.
     * @throws LoginException if unable to obtain a ResourceResolver.
     */
    private Map<String, Object> getResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "schedulertestuser");
        return param;
    }
}
