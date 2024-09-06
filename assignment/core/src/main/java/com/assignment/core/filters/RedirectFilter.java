package com.assignment.core.filters;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Filter.class, immediate = true)
public class RedirectFilter implements Filter {

    @Activate
    @Modified
    public void activate() {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String oldUrl = httpRequest.getRequestURI();
        String newUrl = getNewUrlFromOldUrl(oldUrl);

        if (newUrl != null) {
            httpResponse.sendRedirect(newUrl);
            return;
        }

        chain.doFilter(request, response);
    }

    private String getNewUrlFromOldUrl(String oldUrl) {
        // Logic to map old URLs to new URLs
        if (oldUrl.startsWith("/articles/")) {
            String blogId = oldUrl.substring("/articles/".length());
            String lang = "en";
            String region = "us";

            // Extract query parameters if present
            String[] parts = oldUrl.split("\\?");
            if (parts.length > 1) {
                String query = parts[1];
                for (String param : query.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        if ("lang".equals(keyValue[0])) {
                            lang = keyValue[1];
                        } else if ("region".equals(keyValue[0])) {
                            region = keyValue[1];
                        }
                    }
                }
            }

            // Construct the new URL based on language and region
            return String.format("/content/projectName/%s/%s/blogs/%s.html", region, lang, blogId);
        }

        // Default redirection if no matching pattern found
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}
