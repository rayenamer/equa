package com.rayen.loanManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private Long userId;
    private Double amount;
    private Double interestRate;
    private String status; // PENDING, APPROVED, REJECTED, PAID
    private LocalDate dueDate;

    @OneToOne
    @JoinColumn(name = "insurance_id")
    private MicroInsurance insurance;

    @ManyToOne
    @JoinColumn(name = "credit_score_id")
    private CreditScore creditScore;

    public void approveLoan() {
        this.status = "APPROVED";
    }

    public Double calculateInterest() {
        return amount * interestRate;
    }

}

