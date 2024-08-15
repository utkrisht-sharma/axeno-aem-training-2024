
package com.assignment.core.servlets;

import com.assignment.core.models.RequestParameters;
import org.apache.sling.api.resource.*;
import com.assignment.core.services.NodeService;
import com.assignment.core.services.SearchService;
import com.assignment.core.services.ValidationService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;

@Component(service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/pagesearch",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=POST"
        })
public class PageSearchServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(PageSearchServlet.class);

    @Reference
    private SearchService searchService;

    @Reference
    private NodeService nodeService;

    @Reference
    private ValidationService validationService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {
                RequestParameters params = new RequestParameters(
                request.getParameter("path"),
                request.getParameter("propertyOne"),
                request.getParameter("propertyOneValue"),
                request.getParameter("propertyTwo"),
                request.getParameter("propertyTwoValue"),
                request.getParameter("save")
        );

        // Validate Parameters
        String validationError = validationService.validateSearchParameters(params);
        if (validationError != null) {
            response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, validationError);
            return;
        }

        try {
            // Task 1: Search for pages
            Pair<List<String>, Integer> searchResults = searchService.searchPages(params);
            List<String> resultPaths = searchResults.getLeft();
            int totalMatches = searchResults.getRight();

            // Task 2: Handle saving or deleting nodes
            nodeService.handleSaveOrDelete(params.getSaveParam(), totalMatches);

            // Send Response
            response.setContentType("application/json");
            response.getWriter().write("{\"topResults\":" + resultPaths + ", \"totalMatches\":" + totalMatches + "}");
        } catch (LoginException | RepositoryException | PersistenceException e) {
            log.error("Error processing request", e);
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
