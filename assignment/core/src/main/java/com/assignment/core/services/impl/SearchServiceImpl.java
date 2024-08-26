package com.assignment.core.service.impl;

import com.assignment.core.services.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Implementation of the SearchService interface.
 * Provides methods to execute page searches and retrieve search results.
 */
@Component(service = SearchService.class)
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    /**
     * Executes a search query to find pages based on the specified parameters.
     * It performs a search to count total matches and retrieve the top 10 results.
     *
     * @param resolver the {@link ResourceResolver} to use for the query.
     * @param searchPath the path where the search should be performed.
     * @param propOne the name of the first property to filter on.
     * @param propOneVal the value of the first property to filter on.
     * @param propTwo the name of the second property to filter on.
     * @param propTwoVal the value of the second property to filter on.
     * @return a {@link JSONObject} containing the search results and additional data.
     * @throws LoginException if there is an issue with the resource resolver login.
     * @throws RepositoryException if there is an issue with the JCR repository.
     */
    @Override
    public JSONObject executePageSearch(ResourceResolver resolver, String searchPath, String propOne, String propOneVal, String propTwo, String propTwoVal) throws LoginException, RepositoryException {
        List<String> foundPaths = new ArrayList<>();
        long matchCount = 0;

        logger.info("Initiating page search...");

        // Query to count total matches
        Map<String, String> totalQueryMap = buildQueryMap(searchPath, propOne, propOneVal, propTwo, propTwoVal);
        totalQueryMap.put("p.limit", "0");

        Query countQuery = queryBuilder.createQuery(PredicateGroup.create(totalQueryMap), resolver.adaptTo(Session.class));
        countQuery.setHitsPerPage(0);
        SearchResult totalResult = countQuery.getResult();
        matchCount = totalResult.getHits().size();
        logger.info("Total matches identified: {}", matchCount);

        // Query to retrieve the top 10 results
        Map<String, String> resultQueryMap = new HashMap<>(totalQueryMap);
        resultQueryMap.put("orderby", "@jcr:content/cq:lastModified");
        resultQueryMap.put("orderby.sort", "desc");
        resultQueryMap.put("p.limit", "10");

        Query topQuery = queryBuilder.createQuery(PredicateGroup.create(resultQueryMap), resolver.adaptTo(Session.class));
        SearchResult result = topQuery.getResult();

        for (Hit hit : result.getHits()) {
            foundPaths.add(hit.getPath());
            logger.info("Result path: {}", hit.getPath());
        }

        // Create JSON response
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("totalMatches", matchCount);

        JSONArray jsonResults = new JSONArray();
        for (String path : foundPaths) {
            jsonResults.put(path);
        }
        jsonResponse.put("topResults", jsonResults);

        return jsonResponse;
    }

    private Map<String, String> buildQueryMap(String searchPath, String propOne, String propOneVal, String propTwo, String propTwoVal) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", searchPath);
        queryMap.put("type", "cq:Page");
        queryMap.put("1_property", "jcr:content/" + propOne);
        queryMap.put("1_property.value", propOneVal);
        queryMap.put("2_property", "jcr:content/" + propTwo);
        queryMap.put("2_property.value", propTwoVal);
        queryMap.put("3_daterange.property", "jcr:content/cq:lastModified");
        queryMap.put("3_daterange.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("3_daterange.upperBound", "2020-12-31T23:59:59.999Z");
        return queryMap;
    }
}
