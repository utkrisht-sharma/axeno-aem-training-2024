package com.assignment.core.services;

/**
 * Service for deleting nodes.
 * This interface defines the method for deleting nodes under a specified root path.
 */
public interface NodeDeletionService {
    /**
     * Deletes nodes under the given root path.
     *
     * @param rootPath the root path under which nodes will be deleted
     */
    void deleteNodes(String rootPath);
}