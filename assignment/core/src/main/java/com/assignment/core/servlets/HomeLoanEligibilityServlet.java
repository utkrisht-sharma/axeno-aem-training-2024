package com.assignment.core.servlets;

import com.assignment.core.constants.RequestParamConstants;
import com.assignment.core.constants.ResponseConstants;
import com.assignment.core.services.EMICalculationService;
import com.assignment.core.services.LoanEligibilityService;
import com.assignment.core.services.ValidationService;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet to handle POST requests for home loan eligibility and EMI calculation.
 */
@Component(
        service = { Servlet.class },
        property = {
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=assignment/components/homeloancalculator",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class HomeLoanEligibilityServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(HomeLoanEligibilityServlet.class);

    @Reference
    private LoanEligibilityService loanEligibilityService;

    @Reference
    private ValidationService validationService;

    @Reference
    private EMICalculationService emiCalculationService;

    /**
     * Handles POST requests to process home loan eligibility and calculate EMI.
     *
     * @param request  The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an I/O error occurs during request handling.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            // Fetch parameters from the request
            String clientName = request.getParameter( RequestParamConstants.PARAM_CLIENT_NAME);
            String clientIncome = request.getParameter(RequestParamConstants.PARAM_CLIENT_INCOME);
            String loanAmount = request.getParameter(RequestParamConstants.PARAM_LOAN_AMOUNT);
            String loanTerm = request.getParameter(RequestParamConstants.PARAM_LOAN_TERM);
            String existingEMIs = request.getParameter(RequestParamConstants.PARAM_EXISTING_EMIS);
            String interestRate = request.getParameter(RequestParamConstants.PARAM_INTEREST_RATE);

            // Validate parameters
            Optional<String> errorMessage = validateParameters(clientName, clientIncome, loanAmount, loanTerm, existingEMIs, interestRate);
            if (errorMessage.isPresent()) {
                LOG.error("Validation failed: {}", errorMessage.get());
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("error", errorMessage.get());
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Convert parameters to appropriate types
            double clientIncomeValue = Double.parseDouble(clientIncome);
            double loanAmountValue = Double.parseDouble(loanAmount);
            int loanTermValue = Integer.parseInt(loanTerm);
            double existingEMIsValue = Double.parseDouble(existingEMIs);
            double interestRateValue = Double.parseDouble(interestRate);

            // Check loan eligibility
            boolean isEligible = loanEligibilityService.isEligible(clientIncomeValue, loanAmountValue, loanTermValue, existingEMIsValue, interestRateValue);

            // Calculate EMI
            double emi = emiCalculationService.calculateEMI(loanAmountValue, interestRateValue, loanTermValue);
            jsonResponse.addProperty(ResponseConstants.CLIENT_NAME, clientName);
            jsonResponse.addProperty(ResponseConstants.ELIGIBLE, isEligible);
            jsonResponse.addProperty(ResponseConstants.EMI, isEligible ? String.format("%.2f", emi) : "N/A");
            jsonResponse.addProperty(ResponseConstants.MESSAGE, "Loan Approved");

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            LOG.error("Error processing home loan eligibility", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "An error occurred while processing the request.");
            response.getWriter().write(jsonResponse.toString());
        }
    }

    /**
     * Validates the request parameters.
     *
     * @return {@code Optional} containing an error message if validation fails;
     *         {@code Optional.empty()} if all parameters are valid.
     */
    private Optional<String> validateParameters(String clientName, String clientIncome, String loanAmount, String loanTerm, String existingEMIs, String interestRate) {
        Optional<String> errorMessage;

        if ((errorMessage = validationService.validateClientName(clientName)).isPresent()) return errorMessage;
        if ((errorMessage = validationService.validateClientIncome(clientIncome)).isPresent()) return errorMessage;
        if ((errorMessage = validationService.validateLoanAmount(loanAmount)).isPresent()) return errorMessage;
        if ((errorMessage = validationService.validateLoanTerm(loanTerm)).isPresent()) return errorMessage;
        if ((errorMessage = validationService.validateExistingEMIs(existingEMIs)).isPresent()) return errorMessage;
        if ((errorMessage = validationService.validateInterestRate(interestRate)).isPresent()) return errorMessage;

        return Optional.empty();
    }
}
