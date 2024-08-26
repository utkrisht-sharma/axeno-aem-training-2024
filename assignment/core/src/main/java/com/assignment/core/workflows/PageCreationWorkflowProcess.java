package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.assignment.core.models.PageData;
import com.assignment.core.services.CsvReaderService;
import com.assignment.core.services.PageCreationService;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Workflow process implementation for creating pages in AEM based on CSV data.
 * This workflow process reads a CSV file, extracts page data, and creates pages
 * in the AEM repository using the provided services.
 */
@Component(service = WorkflowProcess.class, immediate = true)
public class PageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(PageCreationWorkflowProcess.class);

    @Reference
    private CsvReaderService csvReaderService;

    @Reference
    private PageCreationService pageCreationService;

    /**
     * Executes the workflow process to create pages based on the data from a CSV file.
     *
     * @param workItem        The current {@link WorkItem} in the workflow process.
     * @param workflowSession The {@link WorkflowSession} associated with the current workflow instance.
     * @param args            The {@link MetaDataMap} containing any arguments or metadata passed to the workflow process.
     */

    // Adapt the workflow session to a ResourceResolver
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) {

        ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
        if (resolver == null) {
            log.error("Could not adapt WorkflowSession to ResourceResolver");
            return;
        }

        String csvFilePath = workItem.getWorkflowData().getPayload().toString();

        List<PageData> pages = csvReaderService.readCSV(resolver, csvFilePath);
        if (pages.isEmpty()) {
            log.warn("No pages found in CSV");
            return;
        }

        for (PageData pageData : pages) {
            try {
                pageCreationService.createPage(resolver, pageData);
            } catch (RepositoryException | PersistenceException e) {
                log.error("Error creating page at {}: {}", pageData.getPath(), e.getMessage());
            }
        }

        try {
            resolver.commit();
        } catch (PersistenceException e) {
            log.error("Error committing changes: {}", e.getMessage());
        }
    }
}

