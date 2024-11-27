package com.assignment.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = SlackAuthenticationConfigImpl.class, immediate = true)
@Designate(ocd = SlackAuthenticationConfigImpl.SlackAuthenticationConfiguration.class)
public class SlackAuthenticationConfigImpl {
    @ObjectClassDefinition(name = "Slack Authentication Config")
    public @interface SlackAuthenticationConfiguration {
        @AttributeDefinition(name = "Client ID", description = "OAuth Client ID", type = AttributeType.STRING) String clientID() default "";

        @AttributeDefinition(name = "Client Secret", description = "OAuth Client Secret", type = AttributeType.STRING) String clientSecret() default "";

        @AttributeDefinition(name = "Redirect URI", description = "Add Redirect URI after authentication", type = AttributeType.STRING) String redirectUri() default "";

        @AttributeDefinition(name = "Scope", description = "Add Scope", type = AttributeType.STRING) String scope() default "channels:read,im:read, mpim:read";

        @AttributeDefinition(name = "State", description = "Add State", type = AttributeType.STRING) String state() default "defaultState";

        @AttributeDefinition(name = "BaseLogin URL", description = "Add BaseLogin URL", type = AttributeType.STRING) String baseLoginUrl() default "";

        @AttributeDefinition(name = "Response Type", description = "Add Response Type", type = AttributeType.STRING) String response_type() default "CODE";

        @AttributeDefinition(name = "Token URL", description = "Add Access Token Url", type = AttributeType.STRING) String tokenUrl() default "";

        @AttributeDefinition(name = "Channel List URL", description = "Add channel List Url", type = AttributeType.STRING) String channelListUrl() default "";
    }

    /**
     * The Query Parameter
     */
    private static final String queryParameter = "?client_id=%s&redirect_uri=%s&state=%s&scope=%s&response_type=%s";
    /**
     * The clientID
     */
    private String clientID;
    /**
     * The clientSecret
     */
    private String clientSecret;
    /**
     * The redirectUri
     */
    private String redirectUri;
    /**
     * The scope
     */
    private String scope;
    /**
     * The  state
     */
    private String state;
    /**
     * The baseLoginUrl
     */
    private String baseLoginUrl;
    /**
     * The response_type
     */
    private String response_type;
    /**
     * The tokenUrl
     */
    private String tokenUrl;
    /**
     * The channelListUrl
     */
    private String channelListUrl;

    @Activate
    @Modified
    protected void activate(SlackAuthenticationConfigImpl.SlackAuthenticationConfiguration configuration) {
        this.clientID = configuration.clientID();
        this.clientSecret = configuration.clientSecret();
        this.redirectUri = configuration.redirectUri();
        this.scope = configuration.scope();
        this.state = configuration.state();
        this.baseLoginUrl = configuration.baseLoginUrl();
        this.response_type = configuration.response_type();
        this.tokenUrl = configuration.tokenUrl();
        this.channelListUrl = configuration.channelListUrl();
    }

    /**
     * Gets the client ID for OAuth authentication.
     * @return the client ID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * Gets the client secret for OAuth authentication.
     * @return the client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Gets the redirect URI for OAuth authentication.
     * @return the redirect URI
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * Gets the scope of the OAuth request.
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Gets the state parameter for OAuth authentication.
     * @return the state parameter
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the response type for the OAuth request.
     * @return the response type
     */
    public String getResponse_type() {
        return response_type;
    }

    /**
     * Gets the base URL for the OAuth login request.
     * @return the base login URL
     */
    public String getBaseLoginUrl() {
        return baseLoginUrl;
    }

    /**
     * Constructs and returns the full login URL for OAuth authentication.
     * @return the login URL with query parameters
     */
    public String getLoginUrl() {
        String loginUrl = baseLoginUrl + queryParameter;
        return String.format(loginUrl, this.clientID, this.redirectUri, this.state, this.scope, this.response_type);
    }

    /**
     * Gets the token URL for OAuth token exchange.
     * @return the token URL
     */
    public String getTokenUrl() {
        return tokenUrl;
    }

    /**
     * Gets the URL to retrieve the list of channels for OAuth integration.
     * @return the channel list URL
     */
    public String getChannelListUrl() {
        return channelListUrl;
    }
}

