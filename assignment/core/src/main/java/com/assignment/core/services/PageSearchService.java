package com.assignment.core.services;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.RepositoryException;
import java.util.Map;

public interface PageSearchService {
    /**
     * Finds pages based on provided search criteria.
     */
    JSONObject findPages(ResourceResolver resolver, Map<String,String>parameters) throws RepositoryException;

    /**
     * Get Pages Path from SearchResult.
     */
    JSONObject getPathsFromSearchResults(SearchResult result) throws RepositoryException;

}
