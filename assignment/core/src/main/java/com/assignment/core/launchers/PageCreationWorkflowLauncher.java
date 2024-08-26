package com.assignment.core.launchers;


import com.adobe.granite.workflow.launcher.WorkflowLauncher;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;


@Component(
        service = PageCreationWorkflowLauncher.class,
        immediate = true
)
public class PageCreationWorkflowLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(PageCreationWorkflowLauncher.class);
    private static final String RUNTIME_MODEL_PATH = "/var/workflow/models/create-page";
    private static final String DESIGN_MODEL_PATH = "/conf/global/settings/workflow/models/create-page";
    private static final String LAUNCHER_PATH = "/conf/global/settings/workflow/launcher/config";
    private static final String LAUNCHER_NAME = "pagecreationlauncher";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    protected void activate() {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            createOrUpdateWorkflowLauncher(resourceResolver);
        } catch (PersistenceException | LoginException e) {
            LOG.error("Error creating or updating workflow launcher", e);
        }
    }

    private ResourceResolver getServiceResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "workflowserviceuser");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

    private void createOrUpdateWorkflowLauncher(ResourceResolver resourceResolver) throws PersistenceException, LoginException {
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

    private void createWorkflowLauncher(ResourceResolver resourceResolver, Resource rootResource) throws PersistenceException, LoginException {
        Map<String, Object> properties = getLauncherProperties();
        resourceResolver.create(rootResource, LAUNCHER_NAME, properties);
    }

    private void updateWorkflowLauncher(Resource launcherResource) throws LoginException {
        ModifiableValueMap properties = launcherResource.adaptTo(ModifiableValueMap.class);
        if (properties != null) {
            properties.putAll(getLauncherProperties());
        }
    }

    private Map<String, Object> getLauncherProperties() throws LoginException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jcr:primaryType", "cq:WorkflowLauncher");
        properties.put("eventType", 1L);
        properties.put("workflow", getWorkflowModelPath(getServiceResourceResolver()));
        properties.put("description", "");
        properties.put("glob", "/content/dam/assignment");
        properties.put("nodetype", "dam:Asset");
        properties.put("runModes", "author");
        properties.put("enabled", true);
        return properties;
    }



    private String getWorkflowModelPath(ResourceResolver resourceResolver) {
        Resource runtimeModelResource = resourceResolver.getResource(RUNTIME_MODEL_PATH);
        if (runtimeModelResource != null) {
            return RUNTIME_MODEL_PATH;
        } else {
            LOG.warn("Runtime workflow model not found. Using design model instead.");
            return DESIGN_MODEL_PATH;
        }
    }
}
