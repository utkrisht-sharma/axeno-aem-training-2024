package com.assignment.core.services.impl;

import com.assignment.core.services.PageCreationService;
import com.assignment.core.services.ProcessCSVService;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of ProcessCSVService for processing CSV files.
 */
@Component(service = ProcessCSVService.class)
public class ProcessCSVServiceImpl implements ProcessCSVService {

    private static final Logger log = LoggerFactory.getLogger(ProcessCSVServiceImpl.class);

    @Reference
    private PageCreationService pageCreationService;

    @Override
    public void processCSV(Resource resource, ResourceResolver resourceResolver) throws IOException {
        Asset asset = resource.adaptTo(Asset.class);
        if (asset != null) {
            InputStream inputStream = asset.getOriginal().adaptTo(InputStream.class);
            if(inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    boolean isFirstLine = true;
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    if (pageManager == null) {
                        log.error("PageManager is not available.");
                        return;
                    }

                    log.info("Starting to read the CSV file.");
                    while ((line = reader.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        List<String> values = Arrays.asList(line.split(","));
                        if (values.size() < 5) {
                            log.error("Invalid CSV line, not enough columns: {}", line);
                            continue;
                        }

                        String title = values.get(0).trim();
                        String path = values.get(1).trim();
                        String description = values.get(2).trim();
                        String tags = values.get(3).trim();
                        String thumbnail = values.get(4).trim();

                        log.info("Processing page with title: {}, path: {}", title, path);

                        String validPageName = title.replaceAll("[^\\p{Alnum}_]", "-");

                        pageCreationService.createPage(pageManager, resourceResolver, path, validPageName, title, description, tags, thumbnail);
                    }
                } catch (WCMException | PersistenceException e) {
                    log.error("Error creating pages from CSV", e);
                }
            }else{
                log.error("Failed to obtain InputStream from the asset's original rendition.");
            }
        }else{
            log.error("Resource at payload path is not a DAM Asset: ");
        }
    }
}

