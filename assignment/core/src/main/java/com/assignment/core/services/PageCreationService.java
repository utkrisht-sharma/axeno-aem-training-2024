package com.assignment.core.services;

import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import java.util.List;

/**
 * Service interface for creating pages in a content repository based on CSV data.
 */
public interface PageCreationService {
    /**
     * Creates multiple pages based on the provided CSV data.
     */
    void createPages(List<String[]> csvDataList, ResourceResolver resolver);

    /**
     * Creates a single page based on the provided CSV data.
     */
    void createPage(PageManager pageManager, String[] csvData, ResourceResolver resolver);
}
