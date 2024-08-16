package com.assignment.core.services.impl;

import com.assignment.core.services.SearchService;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
/**
 * Implementation of the SearchService interface.
 * Provides methods to search for pages in the repository based on specified criteria.
 */
@Component(service = SearchService.class, immediate = true)
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public List<String> searchPages(ResourceResolver resolver, String path, String propertyOne,
                                    String propertyOneValue, String propertyTwo, String propertyTwoValue) {

        // List to store the paths of pages that match the search criteria
        List<String> results = new ArrayList<>();

        log.debug("Starting searchPages with path: {}, propertyOne: {}, propertyOneValue: {}, propertyTwo: {}, propertyTwoValue: {}",
                path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);

        // Creating the predicate map to define the search criteria
        Map<String, String> predicate = new HashMap<>();
        predicate.put("path", path);  // Search under the given path
        predicate.put("type", "cq:Page");  // Search for nodes of type 'cq:Page'
        predicate.put("1_property", "jcr:content/" + propertyOne);  // First property to filter by
        predicate.put("1_property.value", propertyOneValue);  // Value of the first property
        predicate.put("2_property", "jcr:content/" + propertyTwo);  // Second property to filter by
        predicate.put("2_property.value", propertyTwoValue);  // Value of the second property
        predicate.put("daterange.property", "jcr:content/cq:lastModified");  // Date range property
        predicate.put("daterange.lowerBound", "2018-01-01");  // Lower bound of the date range
        predicate.put("daterange.upperBound", "2020-12-31");  // Upper bound of the date range
        predicate.put("orderby", "@jcr:content/cq:lastModified");  // Order results by last modified date
        predicate.put("orderby.sort", "desc");  // Sort order: descending
        predicate.put("p.limit", "10");  // Limit the results to 10 pages

        log.debug("Predicate created: {}", predicate);

        // Building and executing the query using the predicate map
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), resolver.adaptTo(Session.class));
        SearchResult searchResult = query.getResult();

        log.debug("Search executed successfully. Number of hits: {}", searchResult.getHits().size());

        // Processing the search results
        searchResult.getHits().forEach(hit -> {
            try {
                results.add(hit.getPath());  // Adding the path of each matching page to the results list
                log.debug("Adding result path: {}", hit.getPath());
            } catch (RepositoryException e) {
                log.error("Error retrieving path from hit", e);  // Logging any exceptions that occur
            }
        });

        log.info("Search completed with {} results.", results.size());
        return results;  // Returning the list of matching page paths

    }

}



