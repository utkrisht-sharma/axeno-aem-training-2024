package com.assignment.core.services;
/**
 * Service interface for managing nodes in the system.
 */
public interface NodeService {
    /**
     * Saves search results with the given total number of matches.
     */
    void saveSearchResults(long totalMatches);
    /**
     * Deletes all saved nodes.
     */
    void deleteSavedNodes();
}
