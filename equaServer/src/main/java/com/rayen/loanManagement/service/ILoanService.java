package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.model.AmortizationRowResponse;
import com.rayen.loanManagement.model.LoanResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ILoanService {

    List<Loan> getAllLoan();
    Loan getLoanById(Long idLoan);
    Loan addLoan(Loan loan);
    void removeLoan(Long idLoan);
    Loan modifyLoan(Loan loan);

    BigDecimal computeMonthlyPayment(float amount, float annualRatePercent, int months);
    void approveLoan(Long loanId);
    List<AmortizationRowResponse> generateAmortization(Long loanId);
    List<AmortizationRowResponse> getAmortization(Long loanId);
    LoanResponse toResponse(Loan loan);
    Loan getEntity(Long loanId);
}
