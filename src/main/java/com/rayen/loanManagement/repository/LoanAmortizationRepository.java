package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.LoanAmortization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanAmortizationRepository extends JpaRepository<LoanAmortization, Long> {

    List<LoanAmortization> findByLoan_LoanIdOrderByPeriodNumberAsc(Long loanId);
    Optional<LoanAmortization> findByLoan_LoanIdAndPeriodNumber(Long loanId, Integer periodNumber);
    void deleteByLoan_LoanId(Long loanId);
}
