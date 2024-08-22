package com.assignment.core.servlets;

import com.assignment.core.models.HomeLoan;
import com.assignment.core.services.HomeLoanService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import org.json.JSONObject;

/**
 * A servlet for processing home loan applications.
 * <p>
 * This servlet handles POST requests to the resource type
 * "assignment/components/homeloan" and processes loan applications
 * based on provided parameters. It checks the client's eligibility
 * for a loan and calculates the EMI (Equated Monthly Installment) if
 * eligible.
 * </p>
 */


@Component(service = {Servlet.class},immediate = true,
        property = {
                "sling.servlet.resourceTypes=assignment/components/homeloan",
                "sling.servlet.methods=POST",
                "sling.servlet.extensions=json"
        })
public class HomeLoanServlet extends SlingAllMethodsServlet {

    /**
     * Logger for this class. Used for logging information, warnings, and errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HomeLoanServlet.class);

    /**
     * JSON object used to construct the response.
     * Initialized once and reused for all responses to improve performance.
     */
    private final JSONObject jsonResponse = new JSONObject();

    /**
     * Reference to the HomeLoanService.
     * This service is injected by the OSGi framework and handles the business logic for loan processing.
     */
    @Reference
    private HomeLoanService homeLoanService;
    /**
     * The parameter name for the client's name.
     */
    private static final String PARAM_CLIENT_NAME = "clientName";

    /**
     * The parameter name for the client's income.
     */
    private static final String PARAM_CLIENT_INCOME = "clientIncome";

    /**
     * The parameter name for the loan amount.
     */
    private static final String PARAM_LOAN_AMOUNT = "loanAmount";

    /**
     * The parameter name for the loan term.
     */
    private static final String PARAM_LOAN_TERM = "loanTerm";

    /**
     * The parameter name for the client's existing EMIs.
     */
    private static final String PARAM_EXISTING_EMIS = "existingEMIs";

    /**
     * The parameter name for the loan interest rate.
     */
    private static final String PARAM_INTEREST_RATE = "interestRate";

    /**
     * The JSON key for the response status.
     */
    private static final String JSON_KEY_STATUS = "status";

    /**
     * The JSON key for the response message.
     */
    private static final String JSON_KEY_MESSAGE = "message";

    /**
     * The JSON key for indicating loan eligibility.
     */
    private static final String JSON_KEY_ELIGIBLE = "eligible";

    /**
     * The JSON key for the calculated EMI value.
     */
    private static final String JSON_KEY_EMI = "emi";


    /**
     * Handles POST requests for loan applications.
     * <p>
     * Processes loan application details from the request and checks
     * eligibility. If eligible, calculates EMI and sends a success
     * response. Otherwise, sends a rejection response.
     * </p>
     *
     * @param request  The SlingHttpServletRequest containing the loan
     *                 application details.
     * @param response The SlingHttpServletResponse used to send the
     *                 response back to the client.
     * @throws ServletException If an error occurs during request handling.
     * @throws IOException      If an I/O error occurs while writing the
     *                          response.
     */


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");



        String clientName = request.getParameter(PARAM_CLIENT_NAME);
        Double clientIncome = parseDouble(request.getParameter(PARAM_CLIENT_INCOME));
        Double loanAmount = parseDouble(request.getParameter(PARAM_LOAN_AMOUNT));
        Integer loanTerm = parseInt(request.getParameter(PARAM_LOAN_TERM));
        Double existingEMIs = parseDouble(request.getParameter(PARAM_EXISTING_EMIS));
        Double interestRate = parseDouble(request.getParameter(PARAM_INTEREST_RATE));

        try {
            if (clientName == null || clientName.isEmpty()) {
                sendErrorResponse(response, "Client name is missing or empty.");
                return;
            }

            if (clientIncome == null || clientIncome < 0) {
                sendErrorResponse(response, "Client income is missing or invalid.");
                return;
            }

            if (loanAmount == null || loanAmount < 0) {
                sendErrorResponse(response, "Loan amount is missing or invalid.");
                return;
            }

            if (loanTerm == null || loanTerm <= 0) {
                sendErrorResponse(response, "Loan term is missing or invalid.");
                return;
            }

            if (existingEMIs == null || existingEMIs < 0) {
                sendErrorResponse(response, "Existing EMIs are missing or invalid.");
                return;
            }

            if (interestRate == null) {
                sendErrorResponse(response, "Interest rate is missing.");
                return;
            }


            LOG.info("Processing loan application for: {}", clientName);

            HomeLoan homeLoan = new HomeLoan(clientIncome, existingEMIs, loanAmount, loanTerm, interestRate);

            if (homeLoanService.isEligible(homeLoan.getClientIncome(), homeLoan.getExistingEMIs(), homeLoan.getLoanAmount(), homeLoan.getLoanTerm(), homeLoan.getInterestRate())) {
                Double emi = homeLoanService.calculateEMI(homeLoan.getLoanAmount(), homeLoan.getLoanTerm(), homeLoan.getInterestRate());
                sendSuccessResponse(response, clientName, emi);
            } else {
                sendRejectionResponse(response, clientName);
            }
        } catch (IOException e) {
            LOG.error("I/O error occurred while processing the loan application Error: {}", e.getMessage(), e);
            sendErrorResponse(response, "An error occurred while processing your request. Please try again later.");
        }
    }

    /**
     * Parses a string to a double. If the string is null or empty,
     * this method will return null.
     *
     * @param value The string to parse.
     * @return The parsed double, or null if the input is null or empty.
     */
    private Double parseDouble(String value) {
        return (value == null || value.isEmpty()) ? null : Double.parseDouble(value);
    }

    /**
     * Parses a string to an integer. If the string is null or empty,
     * this method will throw a NumberFormatException.
     *
     * @param value The string to parse.
     * @return The parsed integer.
     */
    private Integer parseInt(String value) {
        return (value == null || value.isEmpty()) ? null : Integer.parseInt(value);
    }

    /**
     * Sends an error response in JSON format with the specified message.
     *
     * @param response The SlingHttpServletResponse to write the response to.
     * @param message  The error message to include in the response.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private void sendErrorResponse(SlingHttpServletResponse response, String message) throws IOException {
        jsonResponse.put(JSON_KEY_STATUS, "Error");
        jsonResponse.put(JSON_KEY_MESSAGE, message);
        response.getWriter().write(jsonResponse.toString());
    }

    /**
     * Sends a success response in JSON format with the client's name and
     * the calculated EMI.
     *
     * @param response   The SlingHttpServletResponse to write the response to.
     * @param clientName The name of the client.
     * @param emi        The calculated EMI value.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private void sendSuccessResponse(SlingHttpServletResponse response, String clientName, Double emi) throws IOException {
        jsonResponse.put(PARAM_CLIENT_NAME, clientName);
        jsonResponse.put(JSON_KEY_ELIGIBLE, true);
        jsonResponse.put(JSON_KEY_EMI, String.format("%.2f", emi));
        jsonResponse.put(JSON_KEY_MESSAGE, "Loan Approved");
        response.getWriter().write(jsonResponse.toString());
    }

    /**
     * Sends a rejection response in JSON format with the client's name.
     *
     * @param response   The SlingHttpServletResponse to write the response to.
     * @param clientName The name of the client.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private void sendRejectionResponse(SlingHttpServletResponse response, String clientName) throws IOException {
        jsonResponse.put(PARAM_CLIENT_NAME, clientName);
        jsonResponse.put(JSON_KEY_ELIGIBLE, false);
        jsonResponse.put(JSON_KEY_EMI, "N/A");
        jsonResponse.put(JSON_KEY_MESSAGE, "Loan Rejected");
        response.getWriter().write(jsonResponse.toString());

    }
}
