package com.assignment.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;

import javax.jcr.RepositoryException;

public interface NodeService {
    void handleSaveOrDelete(String saveParam, int totalMatches) throws LoginException, PersistenceException, RepositoryException;
}
