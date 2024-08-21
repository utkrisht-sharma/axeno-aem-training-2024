package com.assignment.core.listeners;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Listener for observing resource changes under the /content path.
 * Listens for ADD, REMOVE, and CHANGE events.
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
     * Handles resource change events.
     *
     * @param changes List of resource changes.
     */
    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            LOG.info("Event: {} at path: {} for userId: {}", change.getType(), change.getPath(), change.getUserId());
        }
    }
}
