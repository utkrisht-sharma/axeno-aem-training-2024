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
     */
    boolean isEligible(HomeLoanRequest userData);


    /**
     * Calculates the EMI for the home loan.
     */
    double calculateEMI(HomeLoanRequest userData);

    /**
     * This method extracts the userdata from the request.
     */
    HomeLoanRequest extractRequestData(SlingHttpServletRequest request);

    /**
     * Processes the loan request to determine eligibility and calculate EMI.
     */
    JSONObject processLoanRequest(HomeLoanRequest userData);
}
