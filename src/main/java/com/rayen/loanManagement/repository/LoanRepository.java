package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
