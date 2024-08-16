package com.assignment.core.service.impl;

import com.assignment.core.services.NodeService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

/**
 * Implementation of the NodeService interface.
 * Provides methods for processing node operations, including saving and deleting nodes.
 */
@Component(service = NodeService.class)
public class NodeServiceImpl implements NodeService {

    private static final Logger logger = LoggerFactory.getLogger(NodeServiceImpl.class);

    private static final String USERGEN_PATH = "/content/usergenerated/session20";

    /**
     * Processes the save or delete operation based on the provided flag.
     * Creates or deletes nodes under the specified path and commits the changes.

     */
    @Override
    public void processSaveOrDelete(ResourceResolver resolver, String saveFlag, long matchCount) throws Exception {
        boolean saveNode = Boolean.parseBoolean(saveFlag);
        Resource userGeneratedRes = resolver.getResource("/content/usergenerated");

        if (userGeneratedRes != null) {
            Node userGeneratedNode = userGeneratedRes.adaptTo(Node.class);

            if (userGeneratedNode != null) {
                logger.info("Processing user-generated nodes...");

                if (saveNode) {
                    Node sessionNode;
                    if (!userGeneratedNode.hasNode("session20")) {
                        sessionNode = userGeneratedNode.addNode("session20");
                        logger.info("New session20 node created under /content/usergenerated.");
                    } else {
                        sessionNode = userGeneratedNode.getNode("session20");
                        logger.info("Existing session20 node accessed under /content/usergenerated.");
                    }
                    Node resultNode = sessionNode.addNode("result_" + System.currentTimeMillis());
                    resultNode.setProperty("searchResult", matchCount);
                    logger.info("New result node created with searchResult: {}", matchCount);
                } else {
                    if (userGeneratedNode.hasNode("session20")) {
                        Node sessionNode = userGeneratedNode.getNode("session20");
                        sessionNode.remove();
                        logger.info("Session20 node deleted under /content/usergenerated.");
                    }
                }
            }
        } else {
            logger.warn("User-generated resource not located at path: /content/usergenerated");
        }

        resolver.commit();
        logger.info("Changes have been committed.");
    }
}
