package com.assignment.core.models;

/**
 * Represents a home loan with details such as client income, existing EMIs,
 * loan amount, loan term, and interest rate.
 */
public class HomeLoan {
    private Double clientIncome;
    private Double existingEMIs;
    private Double loanAmount;
    private int loanTerm;
    private Double interestRate;

    /**
     * Default constructor.
     */
    public HomeLoan() {}

    /**
     * Parameterized constructor to initialize a HomeLoan object.
     *
     * @param clientIncome  The income of the client.
     * @param existingEMIs  The existing EMIs of the client.
     * @param loanAmount    The amount of the loan.
     * @param loanTerm      The term of the loan in months.
     * @param interestRate  The interest rate of the loan.
     */
    public HomeLoan(Double clientIncome, Double existingEMIs, Double loanAmount, int loanTerm, Double interestRate) {
        this.clientIncome = clientIncome;
        this.existingEMIs = existingEMIs;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.interestRate = interestRate;
    }

    /**
     * Gets the client's income.
     *
     * @return The client's income.
     */
    public Double getClientIncome() {
        return clientIncome;
    }

    /**
     * Sets the client's income.
     *
     * @param clientIncome The client's income.
     */
    public void setClientIncome(Double clientIncome) {
        this.clientIncome = clientIncome;
    }

    /**
     * Gets the client's existing EMIs.
     *
     * @return The client's existing EMIs.
     */
    public Double getExistingEMIs() {
        return existingEMIs;
    }

    /**
     * Sets the client's existing EMIs.
     *
     * @param existingEMIs The client's existing EMIs.
     */
    public void setExistingEMIs(Double existingEMIs) {
        this.existingEMIs = existingEMIs;
    }

    /**
     * Gets the loan amount.
     *
     * @return The loan amount.
     */
    public Double getLoanAmount() {
        return loanAmount;
    }

    /**
     * Sets the loan amount.
     *
     * @param loanAmount The loan amount.
     */
    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    /**
     * Gets the loan term in months.
     *
     * @return The loan term in months.
     */
    public int getLoanTerm() {
        return loanTerm;
    }

    /**
     * Sets the loan term in months.
     *
     * @param loanTerm The loan term in months.
     */
    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    /**
     * Gets the interest rate of the loan.
     *
     * @return The interest rate of the loan.
     */
    public Double getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the interest rate of the loan.
     *
     * @param interestRate The interest rate of the loan.
     */
    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }
}

