package com.assignment.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.LoginException;
import javax.jcr.RepositoryException;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Service interface for executing page search queries within a specific path and with given properties.
 */
public interface SearchService {

    /**
     * Executes a page search query based on provided parameters.
     *
     * @param resolver the {@link ResourceResolver} used to interact with the JCR repository.
     * @param searchPath the path within the repository where the search should be performed.
     * @param propOne the first property to filter results by.
     * @param propOneVal the value of the first property to filter results by.
     * @param propTwo the second property to filter results by.
     * @param propTwoVal the value of the second property to filter results by.

     */
    Pair<List<String>, Long> executePageSearch(ResourceResolver resolver, String searchPath, String propOne, String propOneVal, String propTwo, String propTwoVal) throws LoginException, RepositoryException;
}
