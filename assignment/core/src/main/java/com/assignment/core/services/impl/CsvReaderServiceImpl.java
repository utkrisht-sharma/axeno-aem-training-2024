package com.assignment.core.services.impl;

import com.assignment.core.models.PageData;
import com.assignment.core.services.CsvReaderService;
import com.day.cq.dam.api.Asset;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link CsvReaderService} interface for reading CSV files and
 * extracting page data to be used in AEM page creation.
 * This service reads a CSV file from the specified path and maps the data to {@link PageData} objects.
 */
@Component(service = CsvReaderService.class, immediate = true)
public class CsvReaderServiceImpl implements CsvReaderService {

    private static final Logger log = LoggerFactory.getLogger(CsvReaderServiceImpl.class);

    @Override
    public List<PageData> readCSV(ResourceResolver resolver, String csvFilePath) {
        List<PageData> pages = new ArrayList<>();

        Resource resource = resolver.getResource(csvFilePath);
        if (resource == null) {
            log.error("CSV file not found at path: {}", csvFilePath);
            return pages;
        }

        Asset asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            log.error("Could not adapt resource to Asset: {}", csvFilePath);
            return pages;
        }

        try (InputStream inputStream = asset.getOriginal().getStream()) {
            if (inputStream == null) {
                log.error("Could not get InputStream from Asset: {}", csvFilePath);
                return pages;
            }

            String csvContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
                String line;
                String cvsSplitBy = ",";

                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] pageDetails = line.split(cvsSplitBy);
                    for (int i = 0; i < pageDetails.length; i++) {
                        pageDetails[i] = pageDetails[i].trim();
                    }

                    // Extract the necessary fields
                    String title = pageDetails.length > 0 ? pageDetails[0] : "";
                    String path = pageDetails.length > 1 ? pageDetails[1] : "";

                    // If the required fields are missing, log a warning and skip this line
                    if (title.isEmpty() || path.isEmpty()) {
                        log.warn("Skipping invalid CSV line due to missing required fields: Title={}, Path={}", title, path);
                        continue;
                    }

                    // Extract optional fields with default values
                    String description = pageDetails.length > 2 ? pageDetails[2] : "";
                    String tags = pageDetails.length > 3 ? pageDetails[3] : "";
                    String thumbnail = pageDetails.length > 4 ? pageDetails[4] : "";

                    // Log info for missing optional fields
                    if (description.isEmpty()) {
                        log.info("Description is missing for page with Title: {} at Path: {}", title, path);
                    }

                    if (tags.isEmpty()) {
                        log.info("Tags are missing for page with Title: {} at Path: {}", title, path);
                    }

                    if (thumbnail.isEmpty()) {
                        log.info("Thumbnail is missing for page with Title: {} at Path: {}", title, path);
                    }

                    // Create a PageData object and add it to the list
                    PageData pageData = new PageData(title, path, description, tags, thumbnail);
                    pages.add(pageData);

                    log.info("Added page data from CSV: Title={}, Path={}", title, path);
                }
            } catch (IOException e) {
                log.error("Error reading CSV content: {}", e.getMessage(), e);
            }

        } catch (IOException e) {
            log.error("Error getting InputStream from Asset: {}", e.getMessage(), e);
        }

        return pages;
    }
}



