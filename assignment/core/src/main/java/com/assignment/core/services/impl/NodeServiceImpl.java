
package com.assignment.core.services.impl;

import com.assignment.core.services.NodeService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component(service = NodeService.class)
public class NodeServiceImpl implements NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

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
                log.error(" resource not found at path: /content/usergenerated");
            }

            resourceResolver.commit();
            log.info("Changes persisted to the repository.");
        }
    }


    private Map<String, Object> createServiceUserMap() {
        Map<String, Object> serviceUserMap = new HashMap<>();
        serviceUserMap.put(ResourceResolverFactory.SUBSERVICE, "datawriteservice");
        return serviceUserMap;
    }
}
