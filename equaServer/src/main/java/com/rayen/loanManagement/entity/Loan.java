package com.rayen.loanManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private Long userId;
    private float amount;
    private float interestRate;
    private String status;
    private LocalDate dueDate;
    private Integer durationMonths;
    private LocalDate startDate;

    @Column(precision = 18, scale = 2)
    private BigDecimal monthlyPayment;

    public void approveLoan() {
        this.status = "APPROVED";
    }

    public float calculateInterest() {
        return amount * interestRate / 100;
    }
}
