package com.assignment.core.services.Impl;

import com.assignment.core.services.NodeService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link NodeService} interface for managing nodes.
 */
@Component(service = NodeService.class)
public class NodeServiceImpl implements NodeService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeServiceImpl.class);
    private static final String BASE_PATH = "/content/usergenerated/session20";

    @Reference
    private transient ResourceResolverFactory resourceResolverFactory;

    /**
     * Saves search results with the given total number of matches.
     */
    @Override
    public void saveSearchResults(long totalMatches) {
        LOG.info("Saving search results with total matches: {}", totalMatches);
        try (ResourceResolver resolver = getResourceResolver()) {
            Resource baseResource = resolver.getResource(BASE_PATH);
            if (baseResource == null) {
                LOG.debug("Base resource does not exist, creating new resource at {}", BASE_PATH);
                baseResource = resolver.create(resolver.getResource("/content/usergenerated"), "session20", new HashMap<>());
            }
            String nodeName = "result" + Calendar.getInstance().getTimeInMillis();
            resolver.create(baseResource, nodeName, Map.of("searchResult", totalMatches));
            resolver.commit();
            LOG.info("Search results saved under node: {}", nodeName);
        } catch (PersistenceException e) {
            LOG.error("Failed to save search results", e);
        }
    }

    /**
     * Deletes all nodes under the base path.
     */
    @Override
    public void deleteSavedNodes() {
        LOG.info("Deleting all nodes under {}", BASE_PATH);
        try (ResourceResolver resolver = getResourceResolver()) {
            Resource baseResource = resolver.getResource(BASE_PATH);
            if (baseResource != null) {
                resolver.delete(baseResource);
                resolver.commit();
                LOG.info("All nodes under {} deleted successfully", BASE_PATH);
            } else {
                LOG.warn("Base resource {} does not exist, no nodes to delete", BASE_PATH);
            }
        } catch (PersistenceException e) {
            LOG.error("Failed to delete nodes", e);
        }
    }

    /**
     * Retrieves a {@link ResourceResolver} from the resource resolver factory.
     */
    private ResourceResolver getResourceResolver() {
        try {
            return resourceResolverFactory.getServiceResourceResolver(null);
        } catch (LoginException e) {
            LOG.error("Failed to get ResourceResolver", e);
            throw new RuntimeException("Failed to get ResourceResolver");
        }
    }
}
