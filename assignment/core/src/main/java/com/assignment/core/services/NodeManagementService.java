package com.assignment.core.services;

import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

public interface NodeManagementService {

    /**
     * Creates a session node and saves the search result count under the specified node.
     *
     * @param resolver the ResourceResolver
     * @param totalMatches the total number of matches found
     * @throws RepositoryException if there is an error creating the node
     */
    void createOrUpdateSessionNode(ResourceResolver resolver, int totalMatches) throws RepositoryException, PersistenceException;

    /**
     * Deletes all nodes under the specified session node.
     *
     * @param resolver the ResourceResolver
     * @throws RepositoryException if there is an error deleting the nodes
     */
    void deleteSessionNodes(ResourceResolver resolver) throws RepositoryException, PersistenceException;
}
