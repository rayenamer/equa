package com.rayen.loanManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "loan_repayments")
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    private Long userId;
    private Integer periodNumber;

    @Column(precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Column(precision = 18, scale = 2)
    private BigDecimal expectedAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal penaltyAmount;

    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String paymentStatus; // ON_TIME / LATE / PARTIAL / MISSED
}
