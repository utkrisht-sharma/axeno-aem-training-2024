package com.assignment.core.workflows;

import com.assignment.core.services.ProcessCSVService;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * Workflow process for creating pages from a CSV file.
 */
@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class CSVPageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CSVPageCreationWorkflowProcess.class);

    @Reference
    private ProcessCSVService processCSVService;

    /**
     * Executes the workflow process to create pages from a CSV file.
     *
     * @param workItem       The current workflow item.
     * @param workflowSession The current workflow session.
     * @param metaDataMap    The workflow metadata map.
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        try (ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class)) {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            if (resourceResolver != null) {
                log.info("Starting process for payload: {}", payloadPath);
                Resource resource = resourceResolver.getResource(payloadPath);
                if (resource != null) {
                    processCSVService.processCSV(resource, resourceResolver);
                }else{
                    log.error("Resource not found at path: {}",payloadPath);
                }
            } else {
                log.error("ResourceResolver could not be obtained.");
            }
        } catch (IOException e) {
            log.error("Error during CSV Page Creation Workflow Process", e);
        }
    }
}
