package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;

import java.util.List;

public interface ILoanService {

    List<Loan> getAllLoan();
    Loan getLoanById(Long idLoan);
    Loan addLoan(Loan loan);
    void removeLoan(Long idLoan);
    Loan modifyLoan(Loan loan);
}
