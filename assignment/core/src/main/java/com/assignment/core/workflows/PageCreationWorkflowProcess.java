package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.assignment.core.models.PageData;
import com.assignment.core.services.CsvReaderService;
import com.assignment.core.services.PageCreationService;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Workflow process implementation for creating pages in AEM based on CSV data.
 * This workflow process reads a CSV file, extracts page data, and creates pages
 * in the AEM repository using the provided services. Additionally, it includes
 * the creation and management of a runtime model and design model for each workflow instance.
 */
@Component(service = WorkflowProcess.class, immediate = true)
public class PageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(PageCreationWorkflowProcess.class);
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CsvReaderService csvReaderService;

    @Reference
    private PageCreationService pageCreationService;

    /**
     * Executes the workflow process to create pages based on the data from a CSV file.
     * It manages the runtime model and design model associated with the workflow.
     *
     * @param workItem        The current {@link WorkItem} in the workflow process.
     * @param workflowSession The {@link WorkflowSession} associated with the current workflow instance.
     * @param args            The {@link MetaDataMap} containing any arguments or metadata passed to the workflow process.
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) {

        // Adapt the workflow session to a ResourceResolver
        ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
        if (resolver == null) {
            log.error("Could not adapt WorkflowSession to ResourceResolver");
            return;
        }

        String csvFilePath = workItem.getWorkflowData().getPayload().toString();
        log.info("Processing CSV file at path: {}", csvFilePath);

        // Read the CSV file and retrieve page data
        List<PageData> pages = csvReaderService.readCSV(resolver, csvFilePath);
        if (pages.isEmpty()) {
            log.warn("No pages found in CSV");
            return;
        }

        try {
            // Create and manage runtime model
            WorkflowModel runtimeModel = workflowSession.createNewModel("Page Creation Workflow Model_" + System.currentTimeMillis());
            log.info("Created runtime model with ID: {}", runtimeModel.getId());

            // Get the root (start) and end nodes of the workflow model
            WorkflowNode startNode = runtimeModel.getRootNode();
            WorkflowNode endNode = runtimeModel.getEndNode();
            log.info("Retrieved start node and end node for runtime model");

            // Create a process step node to handle the page creation logic
            WorkflowNode processStepNode = runtimeModel.createNode("Page Creation Step", WorkflowNode.TYPE_PROCESS, String.valueOf(startNode));
            processStepNode.getMetaDataMap().put("PROCESS_AUTO_ADVANCE", true);
            processStepNode.getMetaDataMap().put("PROCESS", "com.assignment.core.workflows.PageCreationWorkflowProcess");

            log.info("Created process step node for page creation");

            // Create transitions between start, process step, and end nodes
            runtimeModel.createTransition(startNode, processStepNode, null);
            runtimeModel.createTransition(processStepNode, endNode, null);

            log.info("Created transitions between nodes in runtime model");

            // Iterate over the pages and create them
            for (PageData pageData : pages) {
                try {
                    // Create page using the PageCreationService
                    pageCreationService.createPage(resolver, pageData);
                    log.info("Successfully created page at path: {}", pageData.getPath());

                } catch (RepositoryException | PersistenceException e) {
                    log.error("Error creating page at {}: {}", pageData.getPath(), e.getMessage());
                }
            }

            // Commit the changes
            resolver.commit();
            log.info("Committed changes to the repository");

            // Start the workflow with the runtime model
            workflowSession.startWorkflow(runtimeModel, workItem.getWorkflowData());
            log.info("Started workflow with runtime model ID: {}", runtimeModel.getId());

        } catch (Exception e) {
            log.error("Error in workflow process execution: {}", e.getMessage());
        } finally {
            // Optionally, you can perform cleanup actions, such as saving the session
            if (resolver != null) {
                resolver.close();
                log.info("Closed ResourceResolver");
            }
        }
    }

}
