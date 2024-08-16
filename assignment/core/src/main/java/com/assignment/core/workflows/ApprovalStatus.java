package com.assignment.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

/**
 * This workflow process writes a property to the payload.
 * Where the property is payload-node/jcr:content/approved = true | false
 */
@Component(service = WorkflowProcess.class)
public class ApprovalStatus implements WorkflowProcess {

    private static final String TYPE_JCR_PATH = "JCR_PATH";

    @Override
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        WorkflowData workflowData = item.getWorkflowData();
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            String path = workflowData.getPayload().toString() + "/jcr:content";
            try (ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class)) {
                Resource resource = resolver.getResource(path);
                resource.adaptTo(ModifiableValueMap.class).put("approved", true);
                resolver.commit();
            } catch (PersistenceException e) {
                throw new WorkflowException(e.getMessage(), e);
            }
        }
    }
}