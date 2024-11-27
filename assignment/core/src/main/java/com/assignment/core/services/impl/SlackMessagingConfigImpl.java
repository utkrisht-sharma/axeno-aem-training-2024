package com.assignment.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = SlackMessagingConfigImpl.class, immediate = true)
@Designate(ocd = SlackMessagingConfigImpl.SlackMessagingConfiguration.class)
public class SlackMessagingConfigImpl {
    @ObjectClassDefinition(name = "Slack Messaging Configurations")
    public @interface SlackMessagingConfiguration {
        @AttributeDefinition(name = "Slack Bot Token", description = "Add Bot Token for authenticating")
        String slackBotToken() default "";

        @AttributeDefinition(name = "Slack Channel", description = "Add channel for posting messages")
        String slackChannel() default "";

        @AttributeDefinition(name = "Slack API URL", description = "Add Slack API endpoint for posting messages")
        String slackApiUrl() default "";
    }

    /**
     * The slackToken
     */
    private String slackBotToken;
    /**
     * The slackChannel
     */
    private String slackChannel;
    /**
     * The slackApiUrl
     */
    private String slackApiUrl;

    @Activate
    @Modified
    protected void activate(SlackMessagingConfiguration configuration) {
        this.slackBotToken = configuration.slackBotToken();
        this.slackChannel = configuration.slackChannel();
        this.slackApiUrl = configuration.slackApiUrl();
    }

    /**
     * Gets the Slack OAuth token.
     * @return the Slack OAuth token used for authentication
     */
    public String getSlackBotToken() {
        return slackBotToken;
    }

    /**
     * Gets the name of the Slack channel.
     * @return the name of the Slack channel
     */
    public String getSlackChannel() {
        return slackChannel;
    }

    /**
     * Gets the Slack API URL used for API calls.
     * @return the URL of the Slack API endpoint
     */
    public String getSlackApiUrl() {
        return slackApiUrl;
    }
}
