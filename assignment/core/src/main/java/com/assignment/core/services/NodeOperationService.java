package com.assignment.core.services;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

public interface NodeOperationService {
    void selectNodeOperation(ResourceResolver resolver, String save, long totalMatches) throws PersistenceException, RepositoryException;
    void createNode(ResourceResolver resolver,long totalMatches) throws RepositoryException, PersistenceException;
    void deleteNodes(ResourceResolver resolver) throws RepositoryException, PersistenceException;

}
