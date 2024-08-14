package com.assignment.core.services;

import com.assignment.core.loanrequest.HomeLoanRequest;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONObject;

/**
 * Service interface for determining loan eligibility and calculating EMI.
 */

public interface LoanEligibilityService {


    /**
     * Determines if the client is eligible for a home loan.
     *
     * @param userData {HomeLoanRequest} object containing details of the loan request.
     * @return {true} if the client meets the eligibility criteria, {false} otherwise.
     */
    boolean isEligible(HomeLoanRequest userData);


    /**
     * Calculates the EMI for the home loan.
     * This method is called only if the client is eligible for the loan.
     *
     * @param userData The {HomeLoanRequest} object containing details of the loan request.
     * @return The calculated EMI for the home loan.
     */
    double calculateEMI(HomeLoanRequest userData);

    /**
     * This method extracts the userdata from the request.
     */
    HomeLoanRequest extractRequestData(SlingHttpServletRequest request);

    /**
     * Processes the loan request to determine eligibility and calculate EMI.
     *
     * @param userData {HomeLoanRequest} object containing the loan application data.
     * @return A {JSONObject} containing the loan eligibility status and EMI calculation.
     */
    JSONObject processLoanRequest(HomeLoanRequest userData);
}
