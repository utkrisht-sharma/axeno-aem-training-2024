package com.assignment.core.services.Impl;

import com.assignment.core.services.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the SearchService interface that provides methods
 * for searching pages and retrieving top paths from search results.
 */
@Component(service = SearchService.class)
public class git SearchServiceImpl implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    private transient QueryBuilder queryBuilder;

    @Reference
    private transient ResourceResolverFactory resourceResolverFactory;

    /**
     * Searches for pages based on the specified parameters.
     */
    @Override
    public SearchResult searchPages(String path, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue) {
        LOG.debug("Starting search for path={}, propertyOne={}, propertyOneValue={}, propertyTwo={}, propertyTwoValue={}",
                path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", path);
        queryMap.put("property", "jcr:content/" + propertyOne);
        queryMap.put("property.value", propertyOneValue);
        queryMap.put("property.and", "true");
        queryMap.put("property.1_property", "jcr:content/" + propertyTwo);
        queryMap.put("property.1_value", propertyTwoValue);
        queryMap.put("daterange.property", "jcr:content/jcr:lastModified");
        queryMap.put("daterange.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("daterange.upperBound", "2020-12-31T23:59:59.999Z");
        queryMap.put("p.limit", "10");
        queryMap.put("orderby", "@jcr:content/jcr:lastModified");
        queryMap.put("orderby.sort", "desc");

        try (ResourceResolver resourceResolver = getResourceResolver()) {
            LOG.info("Executing search query on path: {}", path);
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
            SearchResult result = query.getResult();
            LOG.info("Search query executed, found {} matches", result.getTotalMatches());
            return result;
        } catch (Exception e) {
            LOG.error("Error during search", e);
            throw new RuntimeException("Search failed.");
        }
    }

    /**
     * Extracts the top 10 paths from the given search results.
     */
    @Override
    public List<String> getTop10Paths(SearchResult searchResult) {
        LOG.debug("Extracting top 10 paths from search results");

        List<String> top10Paths = new ArrayList<>();

        for (Hit hit : searchResult.getHits()) {
            try {
                String path = hit.getPath();
                top10Paths.add(path);
            } catch (RepositoryException e) {
                LOG.error("Error extracting path from search result", e);
                throw new RuntimeException(e);
            }
        }

        return top10Paths;
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
