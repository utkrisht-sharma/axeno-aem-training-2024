package com.assignment.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.apache.sling.api.servlets.ServletResolverConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple Test Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/content/assignment/api/products.json",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET"
        })
public class ProductsServlet extends SlingAllMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String jsonResponse = "{ \"products\": [ { \"id\": 1, \"name\": \"Product A\" } ] }";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
