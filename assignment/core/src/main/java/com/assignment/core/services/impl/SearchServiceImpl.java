package com.assignment.core.services.impl;

import com.assignment.core.models.RequestParameters;
import com.assignment.core.services.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.*;
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
 * Implementation of the SearchService for searching pages based on request parameters.
 */
@Component(
        name ="Page search service",
        service = SearchService.class,
        immediate = true
)
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;



    /**
     * Searches for pages based on the provided parameters.
     *
     * @param params The search parameters.
     * @return A Pair containing a list of result paths and the total number of matches.
     * @throws LoginException If there is an issue with obtaining the ResourceResolver.
     * @throws RepositoryException If there is an issue with the JCR repository.
     */
    public Pair<List<String>, Integer> searchPages(RequestParameters params) throws LoginException, RepositoryException {
        List<String> resultPaths = new ArrayList<>();
        int totalMatches = 0;

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(createServiceUserMap())) {
            log.info("Starting search for pages...");

            // Create the query map
            Map<String, String> queryMap = createQueryMap(params);

            // Execute the query
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
            SearchResult searchResult = query.getResult();

            // Process all results
            totalMatches = searchResult.getHits().size();
            log.info("Total matches found: {}", totalMatches);

            // only the first 10 results
            for (Hit hit : searchResult.getHits()) {
                if (resultPaths.size() < 10) {
                    resultPaths.add(hit.getPath());
                    log.info("Found result: {}", hit.getPath());
                } else {
                    break;
                }
            }
        }

        return Pair.of(resultPaths, totalMatches);
    }




    /**
     * Creates a map of query parameters based on the provided request parameters.
     *
     * @param params The search parameters.
     * @return A map containing query parameters.
     */
    private Map<String, String> createQueryMap(RequestParameters params) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", params.getSearchPath());
        queryMap.put("type", "cq:Page");
        queryMap.put("1_property", "jcr:content/" + params.getPropertyOne());
        queryMap.put("1_property.value", params.getPropertyOneValue());
        queryMap.put("2_property", "jcr:content/" + params.getPropertyTwo());
        queryMap.put("2_property.value", params.getPropertyTwoValue());
        queryMap.put("3_daterange.property", "jcr:content/cq:lastModified");
        queryMap.put("3_daterange.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("3_daterange.upperBound", "2020-12-31T23:59:59.999Z");
        queryMap.put("orderby", "@jcr:content/cq:lastModified");
        queryMap.put("orderby.sort", "desc");
        queryMap.put("p.limit", "-1");
        return queryMap;
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
