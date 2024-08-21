package com.assignment.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;

import javax.jcr.RepositoryException;

/**
 * Service interface for handling node operations.
 */
public interface NodeService {

    /**
     * Handles the save or delete operation for nodes based on the provided parameters.
     *
     */
    void handleSaveOrDelete(String saveParam, int totalMatches) throws LoginException, PersistenceException, RepositoryException;
}
