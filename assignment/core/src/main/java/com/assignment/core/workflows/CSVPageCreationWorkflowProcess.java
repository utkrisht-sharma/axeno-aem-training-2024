package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.commons.logging.Log;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Workflow process for creating pages from a CSV file in DAM.
 */
@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class CSVPageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CSVPageCreationWorkflowProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Executes the workflow process to create pages from a CSV file.
     *
     * @param workItem The work item containing the workflow data.
     * @param workflowSession The session used to execute the workflow.
     * @param metaDataMap Metadata associated with the workflow.
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        ResourceResolver resourceResolver = null;
        try {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

            Resource resource = resourceResolver.getResource(payloadPath);
            if (resource != null) {
                log.info("Resource found at path: {}, Resource Type: {}", payloadPath, resource.getResourceType());

                Asset asset = resource.adaptTo(Asset.class);
                if (asset != null) {
                    Rendition original = asset.getOriginal();
                    InputStream inputStream = original.adaptTo(InputStream.class);

                    if (inputStream != null) {
                        log.info("CSV file loaded successfully, starting to process the file.");
                        processCSV(inputStream, resourceResolver);
                    } else {
                        log.error("Failed to obtain InputStream from the asset's original rendition.");
                    }
                } else {
                    log.error("Resource at path is not a DAM Asset: {}", payloadPath);
                }
            } else {
                log.error("Resource not found at path: {}", payloadPath);
            }
        } catch (RepositoryException | WCMException | IOException e) {
            log.error("Error during CSV Page Creation Workflow Process", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }

    /**
     * Processes the CSV file to create pages.
     *
     * @param inputStream The InputStream of the CSV file.
     * @param resourceResolver The resource resolver for page creation.
     * @throws Exception If an error occurs during CSV processing.
     */
    private void processCSV(InputStream inputStream, ResourceResolver resourceResolver) throws IOException, RepositoryException, WCMException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean isFirstLine = true;

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            log.error("PageManager is not available.");
            return;
        }

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

            String validPageName = createValidPageName(title);

            createPage(pageManager, path, validPageName, title, description, tags, thumbnail);
        }
    }

    /**
     * Creates a valid page name from the given title.
     *
     */
    private String createValidPageName(String title) {
        return title.replaceAll("[^\\p{Alnum}_]", "-");
    }

    /**
     * Creates a page with the given details.
     *
     * @param pageManager  PageManager to create the page.
     * @param path  path where the page will be created.
     * @param pageName  name of the page.
     * @param title  title of the page.
     * @param description  description of the page.
     * @param tags  tags for the page.
     * @param thumbnail thumbnail path for the page from asset.
     */
    private void createPage(PageManager pageManager, String path, String pageName, String title, String description, String tags, String thumbnail)
            throws RepositoryException , WCMException {

        Page page = pageManager.create(path, pageName, "/conf/global/settings/wcm/templates/page", title);
        if (page != null) {
            Node contentNode = page.getContentResource().adaptTo(Node.class);
            if (contentNode != null) {
                contentNode.setProperty("jcr:description", description);
                contentNode.setProperty("cq:tags", tags.split(";"));
                contentNode.setProperty("cq:templateThumbnail", thumbnail);
                log.info("Page created at: {}", page.getPath());
            }
        }
    }

}
