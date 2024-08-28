package com.assignment.core.servlets;

import com.assignment.core.services.InventoryService;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet to handle adding products to the cart.
 *
 */
@Component(
        service = { Servlet.class },
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/addToCart",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class AddToCartServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddToCartServlet.class);

    public static final String PARAM_PRODUCT_TYPE = "productType";
    public static final String PARAM_QUANTITY = "quantity";

    @Reference
    private InventoryService inventoryService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse = new JsonObject();

        try {
            String productType = request.getParameter(PARAM_PRODUCT_TYPE);
            String quantityStr = request.getParameter(PARAM_QUANTITY);


            LOGGER.info("parameter productType: {}, quantity: {}", productType, quantityStr);

            // Validate parameters
            if (quantityStr == null || quantityStr.isEmpty() || productType == null || productType.isEmpty()) {
                LOGGER.warn("Missing or empty parameter.");
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("message", "Parameter is missing or empty.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }


            int quantity = Integer.parseInt(quantityStr);

            // Get available stock from the service
            int availableStock = inventoryService.getAvailableStock(productType);

            if (availableStock == 0) {
                LOGGER.warn("Product type not found: {}", productType);
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("message", "No such product available!");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Check stock availability
            if (quantity <= availableStock) {
                LOGGER.info("Stock available for {}. Quantity: {}. Adding to cart.", productType, quantity);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                jsonResponse.addProperty("message", "Success: Added to cart!");
            } else {
                LOGGER.info("Not enough stock available for {}. Requested: {}, Available: {}", productType, quantity, availableStock);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                jsonResponse.addProperty("message", "Not enough stock available!");
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while adding to cart.", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("message", "An unexpected error occurred: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
