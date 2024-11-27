package com.assignment.core.servlets;

import com.assignment.core.constants.OAuthConstants;
import com.assignment.core.services.OAuthService;
import com.assignment.core.services.SlackChannelService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Component(service = Servlet.class, property = {"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/slack/oauth"})
public class OAuthServlet extends SlingSafeMethodsServlet {
    private static final String CODE = "code";
    private static final Logger logger = LoggerFactory.getLogger(OAuthServlet.class);
    @Reference
    private OAuthService oAuthService;
    @Reference
    private SlackChannelService slackChannelService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter(CODE);
        if (Objects.nonNull(code)) {
            logger.info("Received authorization code: {}", code);
            String accessToken = oAuthService.getAccessToken(code);

            if (Objects.nonNull(accessToken)) {
                List<String> channels = slackChannelService.getChannels(accessToken);

                if (!channels.isEmpty()) {
                    response.getWriter().write(OAuthConstants.Channel + ":\n");
                    channels.forEach(channel -> {
                        try {
                            response.getWriter().write(channel + "\n");
                        } catch (IOException e) {
                            logger.error("Error writing channel name: {}", e.getMessage());
                        }
                    });
                } else {
                    response.getWriter().write("No channels found or failed to fetch channels.");
                }
            }
        }
    }
}