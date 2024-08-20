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

        InputStream inputStream;
        try {
            inputStream = asset.getOriginal().getStream();
            if (inputStream == null) {
                log.error("Could not get InputStream from Asset: {}", csvFilePath);
                return pages;
            }

            String csvContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
                String line;
                String cvsSplitBy = ",";

                // Skip header line if the CSV has headers
                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    // Skip header line
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Split line into components
                    String[] pageDetails = line.split(cvsSplitBy);

                    // Check if the line contains the expected number of columns
                    if (pageDetails.length >= 5) {
                        // Trim spaces from each value
                        for (int i = 0; i < pageDetails.length; i++) {
                            pageDetails[i] = pageDetails[i].trim();
                        }

                        // Create PageData object
                        PageData pageData = new PageData(
                                pageDetails[0], // title
                                pageDetails[1], // path
                                pageDetails[2], // description
                                pageDetails[3], // tags
                                pageDetails[4]  // thumbnail
                        );
                        pages.add(pageData);

                        // Log the added page data
                        log.info("Added page data from CSV: Title={}, Path={}", pageData.getTitle(), pageData.getPath());
                    } else {
                        log.warn("Skipping invalid CSV line: {}", line);
                    }
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
