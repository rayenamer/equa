package com.rayen.loanManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public void approveLoan() {
        this.status = "APPROVED";
    }

    public float calculateInterest() {
        return amount * interestRate / 100;
    }
}
