package com.assignment.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;

/**
 * Interface for node operations, including saving and deleting nodes.
 */
public interface NodeService {

    /**
     * Processes node operations based on the provided flag.
     * Creates or deletes nodes under a specified path and commits the changes.
     *
     * @param resolver the {@link ResourceResolver} to use for node operations.
     * @param saveFlag indicates whether to save or delete nodes.
     * @param matchCount the count of matches found, used to set properties on nodes.
     * @throws PersistenceException if there is an issue with persisting changes.
     */
    void processSaveOrDelete(ResourceResolver resolver, String saveFlag, long matchCount) throws PersistenceException;
}
