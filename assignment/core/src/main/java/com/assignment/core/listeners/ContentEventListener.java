package com.assignment.core.listeners;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * An implementation of {@link ResourceChangeListener} that listens for resource changes
 * under the specified path and logs the changes. This class handles resource additions,
 * removals, and modifications.
 */
@Component(
        service = ResourceChangeListener.class,
        immediate = true,
        property = {
                ResourceChangeListener.PATHS + "=/content",
                ResourceChangeListener.CHANGES + "=ADDED",
                ResourceChangeListener.CHANGES + "=REMOVED",
                ResourceChangeListener.CHANGES + "=CHANGED"
        }
)
public class ContentEventListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ContentEventListener.class);

    /**
     * Called when resource changes occur. Logs the details of each change and handles
     * the change based on its type.
     *
     * @param changes A list of {@link ResourceChange} objects representing the changes.
     */
    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            LOG.info("Resource change detected:");
            LOG.info(" - Type: {}", change.getType());
            LOG.info(" - Path: {}", change.getPath());
            LOG.info(" - Resource Type: {}", change.getType());
            LOG.info(" - User ID: {}", change.getUserId());

            handleEvent(change);
        }
    }

    /**
     * Handles the resource change based on its type.
     *
     * @param change The {@link ResourceChange} object representing the change.
     */
    private void handleEvent(ResourceChange change) {
        switch (change.getType()) {
            case ADDED:
                handleResourceAdded(change);
                break;
            case REMOVED:
                handleResourceRemoved(change);
                break;
            case CHANGED:
                handleResourceModified(change);
                break;
            default:
                LOG.warn("Unhandled change type: {}", change.getType());
                break;
        }
    }

    /**
     * Handles the event where a resource is added.
     *
     * @param change The {@link ResourceChange} object representing the addition.
     */
    private void handleResourceAdded(ResourceChange change) {
        LOG.info("Handling resource addition at path: {}", change.getPath());
        // Add custom logic for resource addition here
    }

    /**
     * Handles the event where a resource is removed.
     *
     * @param change The {@link ResourceChange} object representing the removal.
     */
    private void handleResourceRemoved(ResourceChange change) {
        LOG.info("Handling resource removal at path: {}", change.getPath());
        // Add custom logic for resource removal here
    }

    /**
     * Handles the event where a resource is modified.
     *
     * @param change The {@link ResourceChange} object representing the modification.
     */
    private void handleResourceModified(ResourceChange change) {
        LOG.info("Handling resource modification at path: {}", change.getPath());
        // Add custom logic for resource modification here
    }
}
