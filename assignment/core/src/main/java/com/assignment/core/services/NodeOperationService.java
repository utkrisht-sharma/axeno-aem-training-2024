package com.assignment.core.services;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

public interface NodeOperationService {

    /**
     * Selects node operation based on the 'save' parameter.
     */
    void selectNodeOperation(ResourceResolver resolver, String save, long totalMatches) throws PersistenceException, RepositoryException;

    /**
     * Creates a new node with the search result property.
     */
    void createNode(ResourceResolver resolver,long totalMatches) throws RepositoryException, PersistenceException;

    /**
     * Deletes all nodes under '/content/usergenerated/session20'.
     */
    void deleteNodes(ResourceResolver resolver) throws RepositoryException, PersistenceException;

}
