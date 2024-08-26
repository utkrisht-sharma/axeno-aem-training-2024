package com.assignment.core.launchers;

import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * responsible for creating or updating a workflow launcher
 * for page creation workflows in AEM.
 */
@Component(
        service = PageCreationWorkflowLauncher.class,
        immediate = true
)
public class PageCreationWorkflowLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationWorkflowLauncher.class);
    private static final String RUNTIME_MODEL_PATH = "/var/workflow/models/create-page0";
    private static final String LAUNCHER_PATH = "/conf/global/settings/workflow/launcher/config";
    private static final String LAUNCHER_NAME = "pagecreationlauncher";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Activates the component and initializes the workflow launcher.
     */
    @Activate
    protected void activate() {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            createOrUpdateWorkflowLauncher(resourceResolver);
        } catch (PersistenceException | LoginException e) {
            LOG.error("Error creating or updating workflow launcher", e);
        }
    }

    /**
     * Retrieves a service resource resolver.
     *
     * @return the service resource resolver
     * @throws LoginException if the service resource resolver cannot be obtained
     */
    private ResourceResolver getServiceResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "workflowserviceuser");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

    /**
     * Creates or updates the workflow launcher
     *
     * @throws PersistenceException if an error occurs during repository access
     * @throws LoginException       if a service resource resolver cannot be obtained
     */
    private void createOrUpdateWorkflowLauncher(ResourceResolver resourceResolver) throws PersistenceException{
        Resource rootResource = resourceResolver.getResource(LAUNCHER_PATH);
        if (rootResource == null) {
            LOG.error("Root resource path {} not found", LAUNCHER_PATH);
            return;
        }

        Resource launcherResource = rootResource.getChild(LAUNCHER_NAME);
        if (launcherResource == null) {
            createWorkflowLauncher(resourceResolver, rootResource);
        } else {
            updateWorkflowLauncher(launcherResource);
        }

        resourceResolver.commit();
    }

    /**
     * Creates a new workflow launcher at the specified location.
     *
     */
    private void createWorkflowLauncher(ResourceResolver resourceResolver, Resource rootResource) throws PersistenceException {
        Map<String, Object> properties = getLauncherProperties();
        resourceResolver.create(rootResource, LAUNCHER_NAME, properties);
    }

    /**
     * Updates an existing workflow launcher with the latest properties.
     *
     */
    private void updateWorkflowLauncher(Resource launcherResource)  {
        ModifiableValueMap properties = launcherResource.adaptTo(ModifiableValueMap.class);
        if (properties != null) {
            properties.putAll(getLauncherProperties());
        }
    }

    /**
     * Provides the properties for the workflow launcher configuration.
     *
     */
    private Map<String, Object> getLauncherProperties()  {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jcr:primaryType", "cq:WorkflowLauncher");
        properties.put("eventType", 1L);
        properties.put("workflow", RUNTIME_MODEL_PATH);
        properties.put("description", "");
        properties.put("glob", "/content/dam/assignment");
        properties.put("nodetype", "dam:Asset");
        properties.put("runModes", "author");
        properties.put("enabled", true);
        return properties;
    }
}
