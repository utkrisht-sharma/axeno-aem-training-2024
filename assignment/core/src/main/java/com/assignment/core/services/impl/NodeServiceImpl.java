package com.assignment.core.services.impl;

import com.assignment.core.services.NodeService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the NodeService that handles node creation and deletion.
 */
@Component(
        name = "Node Creation service",
        service = NodeService.class,
        immediate = true
)
public class NodeServiceImpl implements NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Creates or deletes a node based on the provided parameters.
     *
     * @param saveParam  Indicates whether to save or delete the node.
     * @param totalMatches The number of search results matches to the query.
     * @throws LoginException If there is an issue with obtaining the ResourceResolver.
     * @throws PersistenceException If there is an issue with committing changes to the repository.
     */
    @Override
    public void handleSaveOrDelete(String saveParam, int totalMatches) throws LoginException, PersistenceException {
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(createServiceUserMap())) {
            boolean save = Boolean.parseBoolean(saveParam);
            Resource userGeneratedResource = resourceResolver.getResource("/content/usergenerated");

            if (userGeneratedResource != null) {
                Resource session20Resource = resourceResolver.getResource("/content/usergenerated/session20");

                if (save) {
                    // Create or update the session20 node
                    if (session20Resource == null) {
                        session20Resource = resourceResolver.create(userGeneratedResource, "session20", ValueMap.EMPTY);
                        log.info("Created new node");
                    } else {
                        log.info("Using existing node");
                    }

                    // Create a new node with a unique name and set the searchResult property
                    String newNodeName = "result_" + UUID.randomUUID().toString();
                    Map<String, Object> newNodeProperties = new HashMap<>();
                    newNodeProperties.put("searchResult", totalMatches);
                    resourceResolver.create(session20Resource, newNodeName, newNodeProperties);

                    log.info("Created new node with searchResult property: {}", totalMatches);

                } else {
                    // Delete the session20 node if it exists
                    if (session20Resource != null) {
                        resourceResolver.delete(session20Resource);
                        log.info("Deleted node session20");
                    }
                }
            } else {
                log.error("Resource not found at path: /content/usergenerated");
            }

            resourceResolver.commit();
            log.info("Changes persisted to the repository.");
        }
    }

    /**
     * Creates a map for the service user to access the repository.
     *
     * @return A map with service user credentials.
     */
    private Map<String, Object> createServiceUserMap() {
        Map<String, Object> serviceUserMap = new HashMap<>();
        serviceUserMap.put(ResourceResolverFactory.SUBSERVICE, "datawriteservice");
        return serviceUserMap;
    }
}
