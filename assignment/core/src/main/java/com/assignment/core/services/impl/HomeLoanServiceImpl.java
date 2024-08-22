package com.assignment.core.services;

public class HomeLoanServiceImpl implements HomeLoanService{
    @Override
    public boolean isEligible(Double clientIncome, Double existingEMIs, Double loanAmount, int loanTerm, Double interestRate) {
        Double emi = calculateEMI(loanAmount, loanTerm, interestRate);
        Double maxAllowedEMI = clientIncome * 0.5;
        return (existingEMIs + emi) <= maxAllowedEMI;
    }
@Override
public Double calculateEMI(Double loanAmount, int loanTerm, Double interestRate) {
        Double monthlyRate = interestRate / 1200.0;
        Double term = Math.pow(1 + monthlyRate, loanTerm);
        return loanAmount * monthlyRate * term / (term - 1);
    }


}
