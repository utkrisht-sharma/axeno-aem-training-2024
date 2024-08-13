package com.assignment.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
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
@Component(service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=assignment/components/homeloan",
                "sling.servlet.methods=POST",
                "sling.servlet.extensions=json"
        })
public class HomeLoanServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(HomeLoanServlet.class);
    private final JSONObject jsonResponse = new JSONObject();

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

        String clientName = request.getParameter("clientName");
        Double clientIncome = parseDouble(request.getParameter("clientIncome"));
        Double loanAmount = parseDouble(request.getParameter("loanAmount"));
        Integer loanTerm = parseInt(request.getParameter("loanTerm"));
        Double existingEMIs = parseDouble(request.getParameter("existingEMIs"));
        Double interestRate = parseDouble(request.getParameter("interestRate"));

        try {
            if (clientName == null || clientName.isEmpty() || clientIncome == null || loanAmount == null
                    || loanTerm == null || loanTerm <= 0 || existingEMIs == null || interestRate == null
                    || clientIncome < 0 || loanAmount < 0 || existingEMIs < 0) {
                sendErrorResponse(response, "Invalid input parameters.");
                return;
            }

            LOG.info("Processing loan application for: {}", clientName);

            if (isEligible(clientIncome, existingEMIs, loanAmount, loanTerm, interestRate)) {
                Double emi = calculateEMI(loanAmount, loanTerm, interestRate);
                sendSuccessResponse(response, clientName, emi);
            } else {
                sendRejectionResponse(response, clientName);
            }
        } catch (IOException e) {
            LOG.error("I/O error occurred while processing the loan application", e);
            sendErrorResponse(response, "An error occurred while processing your request. Please try again later.");
        }
    }

    /**
     * Checks if the client is eligible for a loan based on their income,
     * existing EMIs, and the new loan parameters.
     * <p>
     * Eligibility is determined by ensuring that the total EMI (including
     * existing EMIs) does not exceed 50% of the client's income.
     * </p>
     *
     * @param clientIncome The client's monthly income.
     * @param existingEMIs The total of existing EMIs.
     * @param loanAmount   The amount of the new loan.
     * @param loanTerm     The tenure of the new loan in months.
     * @param interestRate The annual interest rate for the new loan.
     * @return true if the client is eligible for the loan, false otherwise.
     */
    private boolean isEligible(Double clientIncome, Double existingEMIs, Double loanAmount, int loanTerm, Double interestRate) {
        Double emi = calculateEMI(loanAmount, loanTerm, interestRate);
        Double maxAllowedEMI = clientIncome * 0.5;
        return (existingEMIs + emi) <= maxAllowedEMI;
    }

    /**
     * Calculates the EMI (Equated Monthly Installment) based on the loan
     * amount, loan term, and annual interest rate.
     * <p>
     * The EMI is computed using the formula:
     * EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
     * where P is the loan amount, R is the monthly interest rate, and N is
     * the loan term in months.
     * </p>
     *
     * @param loanAmount   The principal loan amount.
     * @param loanTerm     The tenure of the loan in months.
     * @param interestRate The annual interest rate for the loan.
     * @return The calculated EMI as a double.
     */
    private Double calculateEMI(Double loanAmount, int loanTerm, Double interestRate) {
        Double monthlyRate = interestRate / 1200.0;
        Double term = Math.pow(1 + monthlyRate, loanTerm);
        return loanAmount * monthlyRate * term / (term - 1);
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
        jsonResponse.put("status", "Error");
        jsonResponse.put("message", message);
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
        jsonResponse.put("clientName", clientName);
        jsonResponse.put("eligible", true);
        jsonResponse.put("emi", String.format("%.2f", emi)); // Ensuring the EMI is formatted to 2 decimal places
        jsonResponse.put("message", "Loan Approved");
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
        jsonResponse.put("clientName", clientName);
        jsonResponse.put("eligible", false);
        jsonResponse.put("emi", "N/A");
        jsonResponse.put("message", "Loan Rejected");
        response.getWriter().write(jsonResponse.toString());
    }
}
