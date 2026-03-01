package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.MicroInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MicroInsuranceRepository extends JpaRepository<MicroInsurance, Long> {

    List<MicroInsurance> findByLoan_LoanId(Long loanId);
    List<MicroInsurance> findByUserId(Long userId);
    List<MicroInsurance> findByLoan_LoanIdAndStatus(Long loanId, String status);
    void deleteByLoan_LoanId(Long loanId);
}
