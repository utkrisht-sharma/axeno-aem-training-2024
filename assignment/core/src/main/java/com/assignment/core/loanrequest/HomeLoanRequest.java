package com.assignment.core.loanrequest;

/**
 * Represents a home loan request.
 * This class holds the details of a client's home loan application.
 */
public class HomeLoanRequest {
    private String clientName;
    private double clientIncome;
    private double loanAmount;
    private int loanTerm;
    private double existingEMIs;
    private double interestRate;

    /**
     * Constructs a new {HomeLoanRequest} with the specified details.
     *
     * @param clientName   The name of the client.
     * @param clientIncome The monthly income of the client.
     * @param loanAmount   The amount of the loan requested.
     * @param loanTerm     The term of the loan in months.
     * @param existingEMIs The total amount of existing EMIs per month.
     * @param interestRate The annual interest rate for the loan.
     */
    public HomeLoanRequest(String clientName, double clientIncome, double loanAmount, int loanTerm, double existingEMIs, double interestRate) {
        this.clientName = clientName;
        this.clientIncome = clientIncome;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.existingEMIs = existingEMIs;
        this.interestRate = interestRate;
    }

    /**
     * Returns the name of the client.
     *
     * @return The client's name.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Returns the monthly income of the client.
     *
     * @return The client's monthly income.
     */

    public double getClientIncome() {
        return clientIncome;
    }

    /**
     * Returns the amount of the loan requested.
     *
     * @return The loan amount.
     */

    public double getLoanAmount() {
        return loanAmount;
    }

    /**
     * Returns the term of the loan in months.
     *
     * @return The loan term in months.
     */
    public int getLoanTerm() {
        return loanTerm;
    }

    /**
     * Returns the total amount of existing EMIs per month.
     *
     * @return The existing EMIs.
     */


    public double getExistingEMIs() {
        return existingEMIs;
    }


    /**
     * Returns the annual interest rate for the loan.
     *
     * @return The annual interest rate.
     */
    public double getInterestRate() {
        return interestRate;
    }


}
