package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService implements ILoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<Loan> getAllLoan() {
        return loanRepository.findAll();
    }

    @Override
    public Loan getLoanById(Long idLoan) {
        return loanRepository.findById(idLoan).orElse(null);
    }

    @Override
    public Loan addLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public void removeLoan(Long idLoan) {
        loanRepository.deleteById(idLoan);
    }

    @Override
    public Loan modifyLoan(Loan loan) {
        return loanRepository.save(loan);
    }
}
