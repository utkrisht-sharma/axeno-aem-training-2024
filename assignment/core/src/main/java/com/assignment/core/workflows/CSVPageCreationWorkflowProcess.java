package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class CSVPageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CSVPageCreationWorkflowProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        try (ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class)) {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            if (resourceResolver != null) {
                log.info("Starting process for payload: {}", payloadPath);
                processPayload(resourceResolver, payloadPath);
            } else {
                log.error("ResourceResolver could not be obtained.");
            }
        } catch (RepositoryException | WCMException | IOException e) {
            log.error("Error during CSV Page Creation Workflow Process", e);
        }
    }

    /**
     * Processes the payload and initiates CSV processing.
     *
     * @param resourceResolver The resource resolver.
     * @param payloadPath  path of csv as payload.
     */
    private void processPayload(ResourceResolver resourceResolver, String payloadPath) throws IOException, RepositoryException, WCMException {
        log.info("Processing payload at path: {}", payloadPath);
        Resource resource = resourceResolver.getResource(payloadPath);
        if (resource != null) {
            Asset asset = resource.adaptTo(Asset.class);
            if (asset != null) {
                try (InputStream inputStream = asset.getOriginal().adaptTo(InputStream.class)) {
                    if (inputStream != null) {
                        log.info("CSV file loaded successfully, starting to process the file.");
                        processCSV(inputStream, resourceResolver);
                    } else {
                        log.error("Failed to obtain InputStream from the asset's original rendition.");
                    }
                }
            } else {
                log.error("Resource at path is not a DAM Asset: {}", payloadPath);
            }
        } else {
            log.error("Resource not found at path: {}", payloadPath);
        }
    }

    /**
     * Processes the CSV file and creates pages.
     *
     * @param inputStream      The InputStream of the CSV file.
     * @param resourceResolver The resource resolver.
     */
    private void processCSV(InputStream inputStream, ResourceResolver resourceResolver) throws IOException, RepositoryException, WCMException {
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

                String validPageName = createValidPageName(title);

                createPage(pageManager, resourceResolver, path, validPageName, title, description, tags, thumbnail);
            }
        }
    }

    /**
     * Creates a valid page name from the title.
     *
     */
    private String createValidPageName(String title) {
        return title.replaceAll("[^\\p{Alnum}_]", "-");

    }

    /**
     * Creates a page and sets its properties, including a thumbnail.
     *
     */
    private void createPage(PageManager pageManager, ResourceResolver resourceResolver, String path, String pageName, String title, String description, String tags, String thumbnail)
            throws WCMException, PersistenceException {

        Page page = pageManager.create(path, pageName, "/conf/global/settings/wcm/templates/page", title);
        if (page != null) {
            log.info("Page created successfully at: {}", page.getPath());
            Resource contentResource = page.getContentResource();
            ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);

            if (properties != null) {
                properties.put("jcr:description", description);
                properties.put("cq:tags", tags.split(";"));

                // Creating a node for storing the thumbnail image
                Resource thumbnailResource = resourceResolver.create(contentResource, "thumbnail", ValueMap.EMPTY);
                ModifiableValueMap thumbnailProps = thumbnailResource.adaptTo(ModifiableValueMap.class);
                if (thumbnailProps != null) {
                    thumbnailProps.put("jcr:primaryType", "nt:unstructured");
                    thumbnailProps.put("fileReference", thumbnail);
                    log.info("Thumbnail image set at: {}", thumbnailResource.getPath());
                }
            }
            resourceResolver.commit();
        } else {
            log.error("Failed to create page at path: {}", path);
        }
    }
}
