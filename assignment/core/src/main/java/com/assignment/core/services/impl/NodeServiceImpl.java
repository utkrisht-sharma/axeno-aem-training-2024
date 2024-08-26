package com.assignment.core.service.impl;

import com.assignment.core.services.NodeService;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the NodeService interface.
 * Provides methods for processing node operations, including saving and deleting nodes.
 */
@Component(service = NodeService.class)
public class NodeServiceImpl implements NodeService {

    private static final Logger logger = LoggerFactory.getLogger(NodeServiceImpl.class);

    private static final String USERGEN_PATH = "/content/usergenerated";

    /**
     * Processes the save or delete operation based on the provided flag.
     * Delegates to saveNode() or deleteNode() based on the saveFlag.
     *
     * @param resolver the {@link ResourceResolver} to use for node operations.
     * @param saveFlag indicates whether to save or delete nodes.
     * @param matchCount the count of matches found, used to set properties on nodes.
     * @throws PersistenceException if there is an issue with persisting changes.
     */
    @Override
    public void processSaveOrDelete(ResourceResolver resolver, String saveFlag, long matchCount) throws PersistenceException {
        boolean saveNode = Boolean.parseBoolean(saveFlag);
        Resource userGeneratedRes = resolver.getResource(USERGEN_PATH);

        if (userGeneratedRes != null) {
            logger.info("Processing user-generated resources...");

            try {
                if (saveNode) {
                    saveNode(resolver, userGeneratedRes, matchCount);
                } else {
                    deleteNode(resolver, userGeneratedRes);
                }

                // Commit changes
                resolver.commit();
                logger.info("Changes have been committed.");
            } catch (PersistenceException e) {
                logger.error("Error during node processing", e);
                // Optionally handle rollback or further recovery actions
                throw e;  // Rethrow to ensure proper handling upstream
            }
        } else {
            logger.warn("User-generated resource not located at path: {}", USERGEN_PATH);
        }
    }

    /**
     * Saves a new node or updates an existing one.
     *
     * @param resolver the {@link ResourceResolver} to use for node operations.
     * @param parentResource the parent resource under which to create or update the node.
     * @param matchCount the count of matches to set as a property on the new node.
     * @throws PersistenceException if there is an issue with persisting changes.
     */
    private void saveNode(ResourceResolver resolver, Resource parentResource, long matchCount) throws PersistenceException {
        Resource sessionResource = parentResource.getChild("session20");

        if (sessionResource == null) {
            // Create a new session20 resource if it doesn't exist
            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "nt:unstructured"); // Specify node type
            sessionResource = resolver.create(parentResource, "session20", properties);
            logger.info("New session20 resource created under /content/usergenerated.");
        } else {
            logger.info("Existing session20 resource accessed under /content/usergenerated.");
        }

        // Create a result node with the search result
        Map<String, Object> resultProperties = new HashMap<>();
        resultProperties.put("jcr:primaryType", "nt:unstructured"); // Specify node type
        resultProperties.put("searchResult", matchCount);
        resolver.create(sessionResource, "result_" + System.currentTimeMillis(), resultProperties);
        logger.info("New result resource created with searchResult: {}", matchCount);
    }

    /**
     * Deletes a node if it exists.
     *
     * @param resolver the {@link ResourceResolver} to use for node operations.
     * @param parentResource the parent resource under which to delete the node.
     * @throws PersistenceException if there is an issue with persisting changes.
     */
    private void deleteNode(ResourceResolver resolver, Resource parentResource) throws PersistenceException {
        Resource sessionResource = parentResource.getChild("session20");
        if (sessionResource != null) {
            resolver.delete(sessionResource);
            logger.info("Session20 resource deleted under /content/usergenerated.");
        }
    }
}
