package com.assignment.core.services;

/**
 * Service interface for deleting nodes under a specified JCR path.
 */
public interface NodeDeletionService {

    /**
     * Deletes one child node under the specified root path.
     *
     * @param rootPath The root path under which child nodes will be deleted.
     * @return {@code true} if a node was deleted and more nodes may remain;
     *         {@code false} if no nodes are left to delete.
     */
    boolean deleteNodesUnderPath(String rootPath);
}
