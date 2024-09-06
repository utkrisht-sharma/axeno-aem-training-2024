package com.assignment.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = Servlet.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Products Servlet",
                "sling.servlet.methods=GET",
                "sling.servlet.resourceTypes=assignment/component/page",
                "sling.servlet.extensions=json"
        }
)
public class DispacherTest extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(DispacherTest.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        log.info("doGet method called");

        // Create a sample JSON response (or fetch actual product data)
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("product", "Example Product");
        jsonResponse.put("status", "success");

        // Write the JSON response
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().write("udit");
    }
}
