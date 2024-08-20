package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = WorkflowProcess.class, property = {"process.label=CSV Page Creation Process"})
public class CSVPageCreationWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CSVPageCreationWorkflowProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        ResourceResolver resourceResolver = null;
        try {
            // Get the payload (CSV file path)
            String payloadPath = workItem.getWorkflowData().getPayload().toString();

            // Get resource resolver from workflow session
            resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

            // Load the CSV file from the payload path
            Resource resource = resourceResolver.getResource(payloadPath);
            if (resource != null) {
                log.info("Resource found at path: {}, Resource Type: {}", payloadPath, resource.getResourceType());

                // adapting the resource to an Asset to obtain the InputStream
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
        } catch (Exception e) {
            log.error("Error during CSV Page Creation Workflow Process", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }

    private void processCSV(InputStream inputStream, ResourceResolver resourceResolver) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean isFirstLine = true;

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            log.error("PageManager is not available.");
            return;
        }

        while ((line = reader.readLine()) != null) {
            // Skip the header line
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            // Split the line by commas to extract values
            List<String> values = Arrays.asList(line.split(","));
            if (values.size() < 5) {
                log.error("Invalid CSV line, not enough columns: {}", line);
                continue;
            }

            // Extract details from each line
            String title = values.get(0).trim();
            String path = values.get(1).trim();
            String description = values.get(2).trim();
            String tags = values.get(3).trim();
            String thumbnail = values.get(4).trim();

            // Generate valid and unique page name
            String validAndUniquePageName = createValidAndUniquePageName(pageManager, title, path);

            // Create the page
            createPage( pageManager, path, validAndUniquePageName, title, description, tags, thumbnail);
        }
    }

    private String createValidAndUniquePageName(PageManager pageManager, String title, String parentPath) {
        // Generate a valid page name
        String validPageName = generateValidPageName(title);

        // Ensure the name is unique
        String uniquePageName = validPageName;
        int counter = 1;

        while (pageManager.getPage(parentPath + "/" + uniquePageName) != null) {
            // Append a counter to the page name if it already exists
            uniquePageName = validPageName + "-" + counter++;
        }

        return uniquePageName;
    }

    private String generateValidPageName(String title) {
        // Replace invalid characters and trim the title to create a valid page name
        String validName = title.replaceAll("[^\\p{Alnum}_]", "-"); // Replace non-alphanumeric characters with "-"
        return validName.length() > 50 ? validName.substring(0, 50) : validName; // Limit length to 50 characters
    }

    private void createPage( PageManager pageManager, String path, String pageName, String title, String description, String tags, String thumbnail) {
        try {
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
        } catch (Exception e) {
            log.error("Error creating page with name: {}", pageName, e);
        }
    }

}
