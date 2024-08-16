package com.assignment.core.services;

import com.day.cq.search.result.SearchResult;
import java.util.List;
/**
 * Service interface for searching pages and retrieving search results.
 */
public interface SearchService {
    /**
     * Searches for pages based on specified parameters.
     */
    SearchResult searchPages(String path, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue);
    /**
     * Retrieves the top 10 paths from the given search results.
     */
    List<String> getTop10Paths(SearchResult searchResult);
}
