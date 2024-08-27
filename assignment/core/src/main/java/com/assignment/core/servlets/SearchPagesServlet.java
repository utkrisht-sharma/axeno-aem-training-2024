package com.assignment.core.servlets;

import com.assignment.core.constants.ServletConstants;
import com.assignment.core.services.NodeOperationService;
import com.assignment.core.services.PageSearchService;
import com.assignment.core.services.ValidationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to handle page search and node management operations.
 */
@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths="+ ServletConstants.SEARCH_SERVLET_PATH,
                "sling.servlet.methods="+ HttpConstants.METHOD_POST,
                "sling.servlet.extensions="+ServletConstants.JSON
        }
)
public class SearchPagesServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SearchPagesServlet.class);

    @Reference
    private PageSearchService pageSearchService;
    @Reference
    private ValidationService validation;
    @Reference
    private NodeOperationService operations;

    /**
     * Handles POST requests for searching pages and managing nodes.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOG.info("Handling POST Request");
        Map<String,String>parameters = new HashMap<>();
        PrintWriter writer=response.getWriter();
        parameters.put("path", request.getParameter("path"));
        parameters.put("propertyOne", request.getParameter("propertyOne"));
        parameters.put("propertyOneValue", request.getParameter("propertyOneValue"));
        parameters.put("propertyTwo", request.getParameter("propertyTwo"));
        parameters.put("propertyTwoValue", request.getParameter("propertyTwoValue"));
        parameters.put("save", request.getParameter("save"));
        if(!validation.validateParameters(parameters)){
            LOG.warn("Validation failed");
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            writer.write("Invalid Parameters");
            return;
        }
        try (ResourceResolver resolver = request.getResourceResolver()) {
            JSONObject jsonResponse = pageSearchService.findPages(resolver, parameters);
            operations.selectNodeOperation(resolver,parameters.get("save"),jsonResponse.getLong("totalMatches"));
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
            LOG.info("Search completed successfully.");
        } catch (IOException | RepositoryException e) {
            LOG.error("Error Occur {} ", e.getMessage());
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
