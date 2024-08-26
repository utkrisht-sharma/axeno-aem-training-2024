package com.assignment.core.workflow;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Rendition;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.assignment.core.services.CSVParsingService;
import com.assignment.core.services.PageCreationService;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Workflow process for creating pages from a CSV file.
 * This class implements the WorkflowProcess interface and is responsible for processing
 * a CSV file asset to create pages in the content repository.
 */
@Component(service = WorkflowProcess.class, property = {
        "process.label=Page Creation from CSV"})
public class PageCreationProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CSVParsingService csvParsingService;

    @Reference
    private PageCreationService pageCreationService;

    /**
     * Executes the workflow process to create pages from the CSV file.
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        LOG.info("PageCreationProcess is executing...");

        try (ResourceResolver resolver = getResourceResolver(workflowSession)) {
            if (resolver == null) {
                LOG.error("ResourceResolver is null");
                return;
            }
            String payloadPath = getPayloadPath(workItem);
            Asset asset = getAsset(payloadPath, resolver);
            if (asset != null) {
                processCSVFile(asset, resolver);
            }
        } catch (Exception e) {
            LOG.error("Error during workflow execution", e);
        }
    }

    /**
     * Retrieves the payload path from the work item.
     */
    private String getPayloadPath(WorkItem workItem) {
        String payload = workItem.getWorkflowData().getPayload().toString();
        LOG.info("Payload path: {}", payload);
        return payload;
    }

    /**
     * Adapts the workflow session to a  ResourceResolver.
     */
    private ResourceResolver getResourceResolver(WorkflowSession workflowSession) {
        return workflowSession.adaptTo(ResourceResolver.class);
    }

    /**
     * Retrieves an asset from the repository based on the given path.
     */
    private Asset getAsset(String path, ResourceResolver resolver) {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
        if (assetManager == null) {
            LOG.error("AssetManager is null");
            return null;
        }
        Asset asset = assetManager.getAsset(path);
        if (asset == null) {
            LOG.error("Asset not found at path: {}", path);
        } else {
            LOG.info("Asset found: {}", asset.getPath());
        }
        return asset;
    }

    /**
     * Processes the CSV file from the asset and creates pages based on the CSV data.
     */
    private void processCSVFile(Asset asset, ResourceResolver resolver) {
        try {
            Rendition originalRendition = getOriginalRendition(asset);
            if (originalRendition != null) {
                try (InputStream inputStream = originalRendition.adaptTo(InputStream.class);
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    List<String[]> csvDataList = csvParsingService.parseCSV(bufferedReader);
                    pageCreationService.createPages(csvDataList, resolver);
                }
            }
        }catch (IOException e) {
            LOG.error("IO Error during workflow execution", e);
        } catch (Exception e) {
            LOG.error("Error processing CSV file", e);
        }
    }

    /**
     * Retrieves the original rendition of the asset.
     */
    private Rendition getOriginalRendition(Asset asset) {
        Rendition original = asset.getRendition("original");
        if (original == null) {
            LOG.error("Original rendition not found for asset at path: {}", asset.getPath());
        } else {
            LOG.info("Original rendition found at path: {}", original.getPath());
        }
        return original;
    }
}
