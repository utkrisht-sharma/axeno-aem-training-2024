package com.assignment.core.workflow;

import com.assignment.core.services.SlackMessagingService;
import com.assignment.core.services.impl.SlackMessagingConfigImpl;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.assignment.core.constants.OAuthConstants.SLACK_CHANNEL;
import static com.assignment.core.constants.OAuthConstants.SLACK_TOKEN;

@Component(service = WorkflowProcess.class, property = {"process.label=Send Asset Metadata to Slack"})
public class AssetsMetadataProcess implements WorkflowProcess {
    private static final Logger log = LoggerFactory.getLogger(AssetsMetadataProcess.class);

    @Reference
    private SlackMessagingService slackMessagingService;
    @Reference
    private SlackMessagingConfigImpl slackMessagingConfig;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        log.info("Processing payload at path: {}", payloadPath);

        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        if (Objects.isNull(resourceResolver)) {
            log.error("ResourceResolver could not be adapted from WorkflowSession");
            return;
        }
        Map<String, String> message = new HashMap<>();
        message.put(SLACK_CHANNEL, metaDataMap.get(SLACK_CHANNEL, slackMessagingConfig.getSlackChannel()));
        message.put(SLACK_TOKEN, metaDataMap.get(SLACK_TOKEN, slackMessagingConfig.getSlackBotToken()));
        slackMessagingService.sendAssetMetadataToSlack(payloadPath, resourceResolver, message);
    }
}
