package com.assignment.core.services;

import com.assignment.core.models.RequestParameters;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Service interface for searching pages.
 */
public interface SearchService {

    /**
     * Searches for pages based on the provided parameters and returns the results.
     *
     */
    Pair<List<String>, Integer> searchPages(RequestParameters params) throws LoginException, RepositoryException;
}
