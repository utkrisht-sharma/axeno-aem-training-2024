package com.assignment.core.services;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import java.io.InputStream;
import java.io.IOException;

/**
 * Service interface for processing CSV files.
 */
public interface ProcessCSVService {

    /**
     * Processes the CSV file from the given input stream.
     *
     *@param resource resource of the CSV file.
     * @param resourceResolver The resource resolver.
     * @throws IOException    if an I/O error occurs.
     */
    void processCSV(Resource resource, ResourceResolver resourceResolver) throws IOException;
}
