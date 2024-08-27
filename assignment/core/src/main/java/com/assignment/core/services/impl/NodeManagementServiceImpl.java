package com.assignment.core.services.impl;

import com.assignment.core.services.NodeManagementService;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.UUID;

/**
 * Implementation of the NodeManagementService interface.
 * Provides methods to manage nodes within the repository, including creating, updating,
 * and deleting session-related nodes based on specified criteria.
 */
@Component(service = NodeManagementService.class, immediate = true)
public class NodeManagementServiceImpl implements NodeManagementService {
    private static final Logger LOG = LoggerFactory.getLogger(NodeManagementServiceImpl.class);

    @Override
    public void createOrUpdateSessionNode(ResourceResolver resolver, int totalMatches) throws RepositoryException, PersistenceException {
        LOG.debug("Starting createOrUpdateSessionNode with totalMatches: {}", totalMatches);

        try {
            Resource sessionResource = resolver.getResource("/content/usergenerated/session20");
            if (sessionResource == null) {
                LOG.info("Session20 resource not found, creating new one.");
                Resource parentResource = resolver.getResource("/content/usergenerated");
                if (parentResource == null) {
                    throw new RepositoryException("Parent resource /content/usergenerated not found");
                }
                sessionResource = resolver.create(parentResource, "session20", Collections.singletonMap("jcr:primaryType", "nt:unstructured"));
            }

            Resource resultResource = resolver.create(sessionResource, UUID.randomUUID().toString(), Collections.singletonMap("jcr:primaryType", "nt:unstructured"));
            ModifiableValueMap properties = resultResource.adaptTo(ModifiableValueMap.class);
            if (properties != null) {
                properties.put("searchResult", totalMatches);
                resolver.commit();
                LOG.info("Node created or updated successfully with searchResult: {}", totalMatches);
            } else {
                throw new RepositoryException("Unable to adapt resource to ModifiableValueMap");
            }
        } catch (PersistenceException e) {
            LOG.error("Error persisting changes: {}", e.getMessage());
        } catch (RepositoryException e) {
            LOG.error("Repository error: {}", e.getMessage());

        }
    }

    @Override
    public void deleteSessionNodes(ResourceResolver resolver) throws RepositoryException, PersistenceException {
        LOG.debug("Starting deleteSessionNodes");

        try {
            Resource sessionResource = resolver.getResource("/content/usergenerated/session20");
            if (sessionResource != null) {
                for (Resource childResource : sessionResource.getChildren()) {
                    LOG.debug("Deleting resource: {}", childResource.getPath());
                    resolver.delete(childResource);
                }
                resolver.commit();
                LOG.info("All session nodes deleted successfully.");
            } else {
                LOG.warn("Session20 resource not found, nothing to delete.");
            }
        } catch (PersistenceException e) {
            LOG.error("Error persisting changes while deleting nodes: {}", e.getMessage());

        }
    }
}