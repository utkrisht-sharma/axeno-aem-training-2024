package com.assignment.core.services.impl;

import com.assignment.core.services.NodeOperationService;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component(service= NodeOperationService.class)
public class NodeOperationServiceImpl implements NodeOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeOperationServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectNodeOperation(ResourceResolver resolver, String save, long totalMatches) throws PersistenceException, RepositoryException {
        if ("true".equalsIgnoreCase(save)) {
            createNode(resolver, totalMatches);
        }
        else {
            deleteNodes(resolver);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNode(ResourceResolver resolver, long totalMatches) throws RepositoryException, PersistenceException {

        Resource session20 = resolver.getResource("/content/usergenerated/session20");
        if (Objects.isNull(session20)) {
            session20 = resolver.create(resolver.getResource("/content/usergenerated"), "session20", ValueMap.EMPTY);
        }
        Map<String,Object> properties=new HashMap<>();
        properties.put("searchResult",totalMatches);
        String nodeName = "node-" + UUID.randomUUID().toString();
        resolver.create(session20, nodeName, properties);
        resolver.commit();
        LOG.info("Node Created successfully");

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNodes(ResourceResolver resolver) throws RepositoryException, PersistenceException {
        Resource session20 = resolver.getResource("/content/usergenerated/session20");
        if (!Objects.isNull(session20)) {
            for (Resource child : session20.getChildren()) {
                resolver.delete(child);
            }
            resolver.commit();
            LOG.info("Nodes Deleted Successfully");
        }
        else{
            LOG.warn("Attempting To Delete UnExisted Node");
        }
    }

    }

