package com.assignment.core.models;

import com.assignment.core.services.impl.SlackAuthenticationConfigImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;


@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SlackLoginModel {
    @OSGiService
    private SlackAuthenticationConfigImpl slackAuthenticationConfig;

    public String getLoginUrl() {
        return slackAuthenticationConfig.getLoginUrl();
    }
}
