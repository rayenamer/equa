package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoan_LoanIdOrderByPeriodNumberAsc(Long loanId);
    List<LoanRepayment> findByUserId(Long userId);
    Optional<LoanRepayment> findByLoan_LoanIdAndPeriodNumber(Long loanId, Integer periodNumber);
    long countByLoan_LoanIdAndPaymentStatus(Long loanId, String paymentStatus);
    long countByPaymentStatus(String paymentStatus);
}
