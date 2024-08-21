package com.assignment.core.workflow;


import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.assignment.core.PageMetaData.PageData;
import com.assignment.core.service.PageCreationService;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Workflow process to create pages based on a CSV file provided as payload.
 */

@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class PageCreationProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(PageCreationProcess.class);

    @Reference
    private PageCreationService pageCreationService;

    /**
     * Executes the workflow process to create pages based on the CSV file.
     */
    @Override
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        log.info("Execution of execute method started");
        try (ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class)) {
            String csvFilePath = item.getWorkflowData().getPayload().toString();
            Resource csvResource = resolver.getResource(csvFilePath);

            if (!Objects.isNull(csvResource)) {
                log.info("CSV Resource Is Not Null");
                Asset asset = csvResource.adaptTo(Asset.class);
                if (!Objects.isNull(asset)) {
                    try (InputStream csvInputStream = asset.getOriginal().getStream()) {
                        if (!Objects.isNull(csvInputStream)) {
                            log.info("CSV Input Stream Is Not Null");
                            List<PageData> pages = processCSV(csvInputStream);
                            for (PageData page : pages) {
                                pageCreationService.createPage(page.getPath(), page.getTitle(), page.getDescription(), page.getTags(), page.getThumbnail(), resolver);
                            }
                        } else {
                            log.error("Unable to get InputStream from resource");
                        }

                    }
                } else {
                    log.error("Failed to adapt resource to Asset");
                }

            } else {
                log.error("CSV file not found");
            }
        } catch (Exception e) {
            throw new WorkflowException("Error executing workflow", e);
        }
    }

    /**
     * Processes the CSV input stream and creates pages.
     */
    private List<PageData> processCSV(InputStream csvInputStream) throws IOException {
        log.info("Process CSV Data in processCSV method");
        List<PageData> pages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] pageDetails = line.split(",");
                String title = pageDetails.length > 0 ? pageDetails[0].trim() : "";
                String path = pageDetails.length > 1 ? pageDetails[1].trim() : "";
                String description = pageDetails.length > 2 ? pageDetails[2].trim() : "";
                String tags = pageDetails.length > 3 ? pageDetails[3].trim() : "";
                String thumbnailPath = pageDetails.length > 4 ? pageDetails[4].trim() : "";
                if (!title.isEmpty() && !path.isEmpty()) {
                    pages.add(new PageData(title, path, description, tags, thumbnailPath));
                }
            }
        }
        log.info("CSV File Processed Successfully");
        return pages;
    }
}
