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
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The {@code CSVpageCreationProcess} class implements the {@link WorkflowProcess} interface
 * and handles the creation of AEM pages based on the contents of a CSV file.
 *
 * <p>This workflow process reads a CSV file from the payload path, where each line
 * corresponds to a page to be created..</p>
 */
@Component(service = WorkflowProcess.class, property = {
        "process.label=Workflow for page creation"})
public class CSVpageCreationProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(CSVpageCreationProcess.class);
    private ResourceResolver resolver = null;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        LOG.info("Executing CSVpageCreationProcess...");

        try {
            String payloadPath = getPayloadPath(workItem);
            resolver = getResourceResolver(workflowSession);
            Asset asset = getAsset(payloadPath);

            if (asset != null) {
                processCSVFile(asset);
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
     * Retrieves the ResourceResolver from the WorkflowSession.
     * it will return the ResourceResolver object.
     */
    private ResourceResolver getResourceResolver(WorkflowSession workflowSession) {
        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        if (resourceResolver == null) {
            LOG.error("ResourceResolver is null");
        }
        return resourceResolver;
    }

    /**
     * Retrieves the asset from the given path.
     * It will return The Asset object.
     */
    private Asset getAsset(String path) {
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
    private void processCSVFile(Asset asset) {
        try {
            Rendition original = getOriginalRendition(asset);
            if (original != null) {
                try (InputStream inputStream = original.adaptTo(InputStream.class)) {
                    if (inputStream != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        processCSVLines(bufferedReader);
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
     * @param bufferedReader The BufferedReader object to read the CSV file.
     */
    private void processCSVLines(BufferedReader bufferedReader) {
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
                createPage(pageManager, csvData);
            }
        } catch (Exception e) {
            LOG.error("Error processing CSV lines", e);
        }
    }

    /**
     * Creates a page based on the data provided in the CSV file.
     * @param pageManager The PageManager object used to create pages.
     * @param csvData     The array containing the CSV data for the page.
     */
    private void createPage(PageManager pageManager, String[] csvData) {
        try {
            String title = csvData[0].trim();
            String path = csvData[1].trim();
            String description = csvData[2].trim();
            String tags = csvData[3].trim();
            String thumbnail = csvData[4].trim();

            String pageName = generateValidName(title);
            Page page = pageManager.create(path, pageName, "/conf/global/settings/wcm/templates/page", title);

            if (page != null) {
                LOG.info("Page created successfully: {}/{}", path, title);
                setPageProperties(page, description, tags, thumbnail);
            } else {
                LOG.warn("Page creation failed for title: {}", title);
            }
        } catch (Exception e) {
            LOG.error("Error creating page", e);
        }
    }

    /**
     * Sets the properties on the newly created page.
     * @param page        The page on which to set properties.
     * @param description The description to set on trhe page.
     * @param tags        The tags to set on the pge.
     * @param thumbnail   The thubnail path to set on the page.
     */
    private void setPageProperties(Page page, String description, String tags, String thumbnail) {
        try {
            Node pageContentNode = page.getContentResource().adaptTo(Node.class);
            if (pageContentNode != null) {
                LOG.info("Setting properties for page: {}", page.getTitle());
                pageContentNode.setProperty("jcr:description", description);
                pageContentNode.setProperty("cq:tags", tags.split("/"));
                pageContentNode.setProperty("cq:thumbnail", thumbnail);
            } else {
                LOG.warn("Page content node is null for page: {}", page.getTitle());
            }
        } catch (Exception e) {
            LOG.error("Error setting page properties", e);
        }
    }

    /**
     * Generates a valid page name by replacing invalid characters with hyphens.
     *
     * @param title The title from which to generate the page name.
     * It will return  A valid page name.
     */
    private String generateValidName(String title) {
        return title.replaceAll("[^\\p{Alnum}_]", "-");
    }
}
