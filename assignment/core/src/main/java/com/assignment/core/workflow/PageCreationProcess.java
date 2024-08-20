package com.assignment.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.assignment.core.service.PageCreationService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Workflow process to create pages based on a CSV file provided as payload.
 */

@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class PageCreationProcess implements WorkflowProcess {
    @Reference
    private PageCreationService pageCreationService;

    /**
     * Executes the workflow process to create pages based on the CSV file.

     */
    @Override
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try (ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class)) {
            String csvFilePath = item.getWorkflowData().getPayload().toString();
            Resource csvResource = resolver.getResource(csvFilePath);

            if (!Objects.isNull(csvResource)) {
                InputStream csvInputStream = csvResource.adaptTo(InputStream.class);
                processCSV(csvInputStream, resolver);
            }
        } catch (Exception e) {
            throw new WorkflowException("Error executing workflow");
        }
    }

    /**
     * Processes the CSV input stream and creates pages.
     */
    private void processCSV(InputStream csvInputStream, ResourceResolver resolver) throws Exception {
        try (CSVParser parser = CSVParser.parse(csvInputStream, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : parser) {
                String title = record.get("title");
                String path = record.get("path");
                String description = record.get("description");
                String tags = record.get("tags");

                pageCreationService.createPage(path, title, description, tags, resolver);
            }
        }
    }
}
