package com.assignment.core.services;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;

public interface PageSearchService {
    SearchResult findPages(ResourceResolver resolver, String path, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue);
}
