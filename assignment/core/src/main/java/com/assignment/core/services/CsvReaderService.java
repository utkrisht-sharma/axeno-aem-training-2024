package com.assignment.core.services;

import com.assignment.core.models.PageData;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * Service interface for reading and parsing CSV files.
 * and converting its contents into a list of {@link PageData} objects.
 */
public interface CsvReaderService {

    /**

     * @param resolver    the {@link ResourceResolver} used to access the AEM repository and read the CSV file.
     * @param csvFilePath the file path of the CSV file in the AEM repository.
     * @return a list of {@link PageData} objects parsed from the CSV file. If the file is not found or cannot be read,
     *         an empty list is returned.
     */
    List<PageData> readCSV(ResourceResolver resolver, String csvFilePath);
}
