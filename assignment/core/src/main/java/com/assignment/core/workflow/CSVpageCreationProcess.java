package com.assignment.core.workflow;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Rendition;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CSVpageCreationProcess} class implements the {@link WorkflowProcess} interface
 * and handles the creation of AEM pages based on the contents of a CSV file.
 *
 * <p>This workflow process reads a CSV file from the payload path, where each line
 * corresponds to a page to be created.</p>
 */
@Component(service = WorkflowProcess.class, property = {
        "process.label=Workflow for page creation"})
public class CSVpageCreationProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(CSVpageCreationProcess.class);
    private static final String SERVICE_USER = "workflow-user";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        LOG.info("Executing CSVpageCreationProcess...");

        try (ResourceResolver resolver = getServiceResourceResolver()) {
            if (resolver != null) {
                // Obtain the payload path from the WorkItem
                String payloadPath = getPayloadPath(workItem);

                // Get the asset using the payload path
                Asset asset = getAsset(resolver, payloadPath);

                if (asset != null) {
                    processCSVFile(resolver, asset);
                }
            } else {
                LOG.error("ResourceResolver could not be obtained using service user: {}", SERVICE_USER);
            }
        } catch (Exception e) {
            LOG.error("Exception occurred during workflow execution", e);
        }
    }

    /**
     * Retrieves the payload path from the work item.
     * it will return the payload path as a string.
     */
    private String getPayloadPath(WorkItem workItem) {
        String payload = workItem.getWorkflowData().getPayload().toString();
        LOG.info("Payload path: {}", payload);
        return payload;
    }

    /**
     * Obtains a ResourceResolver using the configured service user.
     * Returns the ResourceResolver object or null if login fails.
     */
    private ResourceResolver getServiceResourceResolver() {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);
        try {
            return resolverFactory.getServiceResourceResolver(authInfo);
        } catch (LoginException e) {
            LOG.error("Error obtaining ResourceResolver for service user: {}", SERVICE_USER, e);
            return null;
        }
    }


    /**
     * Retrieves the asset from the given path.
     * It will return The Asset object.
     */
    private Asset getAsset(ResourceResolver resolver, String path) {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
        if (assetManager == null) {
            LOG.error("AssetManager is null");
            return null;
        }

        Asset asset = assetManager.getAsset(path);
        if (asset == null) {
            LOG.error("Asset not found at payload path: {}", path);
        } else {
            LOG.info("Asset found: {}", asset.getPath());
        }

        return asset;
    }

    /**
     * Processes the CSV file and creates pages accordingly.
     */
    private void processCSVFile(ResourceResolver resolver, Asset asset) {
        try {
            Rendition original = getOriginalRendition(asset);
            if (original != null) {
                try (InputStream inputStream = original.adaptTo(InputStream.class)) {
                    if (inputStream != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        processCSVLines(resolver, bufferedReader);
                    } else {
                        LOG.error("Input stream is null");
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error processing CSV file", e);
        }
    }

    /**
     * Retrieves the original rendition of the asset.
     * Here It will return The Rendition object representing the original rendition.
     */
    private Rendition getOriginalRendition(Asset asset) {
        Rendition original = asset.getRendition("original");
        if (original == null) {
            LOG.error("Rendition not found for asset: {}", asset.getPath());
        } else {
            LOG.info("Rendition found: {}", original.getPath());
        }
        return original;
    }

    /**
     * Processes each line of the CSV file and creates a page for each entry.
     *
     * @param resolver       The ResourceResolver object used to interact with resources.
     * @param bufferedReader The BufferedReader object to read the CSV file.
     */
    private void processCSVLines(ResourceResolver resolver, BufferedReader bufferedReader) {
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.error("PageManager is null");
            return;
        }

        try {
            String line;
            boolean isHeader = true;  // Assume first line is header

            while ((line = bufferedReader.readLine()) != null) {
                if (isHeader) {
                    LOG.info("Skipping header line");
                    isHeader = false;
                    continue;
                }

                LOG.info("Processing line: {}", line);
                String[] csvData = line.split(",");
                createPage(pageManager, csvData, resolver);
            }
        } catch (IOException e) {
            LOG.error("Error reading CSV file", e);
        }
    }

    /**
     * Creates a page based on the data provided in the CSV file.
     *
     * @param pageManager The PageManager object used to create pages.
     * @param csvData     The array containing the CSV data for the page.
     * @param resolver    The ResourceResolver object for accessing resources.
     */
    private void createPage(PageManager pageManager, String[] csvData, ResourceResolver resolver) {
        try {
            String title = csvData[0].trim();
            String path = csvData[1].trim();
            String description = csvData[2].trim();
            String tags = csvData[3].trim();
            String thumbnail = csvData[4].trim();

            String pageName = generateValidName(title);
            Page page = pageManager.create(path, pageName, "/conf/assignment/settings/wcm/templates/page-content", title);

            if (page != null) {
                LOG.info("Page created successfully: {}/{}", path, title);
                setPageProperties(page, description, tags, thumbnail, resolver);
            } else {
                LOG.warn("Page creation failed for title: {}", title);
            }
        } catch (Exception e) {
            LOG.error("Error creating page", e);
        }
    }

    /**
     * Sets the properties on the newly created page.
     *
     * @param page        The page on which to set properties.
     * @param description The description to set on the page.
     * @param tags        The tags to set on the page.
     * @param thumbnail   The thumbnail path to set on the page.
     * @param resolver    The ResourceResolver object for accessing resources.
     */
    private void setPageProperties(Page page, String description, String tags, String thumbnail, ResourceResolver resolver) {
        try {
            Resource pageContentResource = page.getContentResource();
            if (pageContentResource != null) {
                ModifiableValueMap properties = pageContentResource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    LOG.info("Setting properties for page: {}", page.getTitle());
                    properties.put("jcr:description", description);
                    properties.put("cq:tags", tags.split("/"));
                    properties.put("cq:thumbnail", thumbnail);
                    resolver.commit();  // Persist changes
                } else {
                    LOG.warn("ModifiableValueMap is null for page: {}", page.getTitle());
                }
            } else {
                LOG.warn("Page content resource is null for page: {}", page.getTitle());
            }
        } catch (PersistenceException e) {
            LOG.error("Error persisting changes for page properties", e);
        }
    }

    /**
     * Generates a valid page name by replacing invalid characters with hyphens.
     *
     * @param title The title from which to generate the page name.
     *              It will return A valid page name.
     */
    private String generateValidName(String title) {
        return title.replaceAll("[^\\p{Alnum}_]", "-");
    }
}