package com.assignment.core.services;

/**
 * Represents a home loan request containing client information and loan details.
 */
public class HomeLoanRequest {
    private final String clientName;
    private final double clientIncome;
    private final double loanAmount;
    private final int loanTerm;
    private final double existingEMIs;
    private final double interestRate;

    /**
     * Constructs a new HomeLoanRequest with the specified details.
     *
     * @param clientName    The name of the client.
     * @param clientIncome  The income of the client.
     * @param loanAmount    The amount of the loan requested.
     * @param loanTerm      The term of the loan in months.
     * @param existingEMIs  The existing EMIs that the client is paying.
     * @param interestRate  The annual interest rate of the loan.
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
     * @return The name of the client.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Returns the income of the client.
     *
     * @return The income of the client.
     */
    public double getClientIncome() {
        return clientIncome;
    }

    /**
     * Returns the amount of the loan requested.
     *
     * @return The amount of the loan requested.
     */
    public double getLoanAmount() {
        return loanAmount;
    }

    /**
     * Returns the term of the loan in months.
     *
     * @return The term of the loan in months.
     */
    public int getLoanTerm() {
        return loanTerm;
    }

    /**
     * Returns the existing EMIs that the client is paying.
     *
     * @return The existing EMIs.
     */
    public double getExistingEMIs() {
        return existingEMIs;
    }

    /**
     * Returns the annual interest rate of the loan.
     *
     * @return The annual interest rate.
     */
    public double getInterestRate() {
        return interestRate;
    }
}
