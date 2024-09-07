package com.assignment.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A Sling servlet that responds to HTTP GET requests with a JSON object representing a product.
 */
@Component(
        service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/content/assignment/api/product.json",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET"
        }
)
public class ProductServlet extends SlingSafeMethodsServlet {

    /**
     * Handles HTTP GET requests by returning a JSON response containing product details.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("productId", "12345");
        jsonResponse.put("productName", "Sample Product");
        jsonResponse.put("price", "99.99");
        response.getWriter().write(jsonResponse.toString());
    }
}
