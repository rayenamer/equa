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
@Table(name = "loan_amortizations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"loan_id", "period_number"})
})
public class LoanAmortization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    private Integer periodNumber;
    private LocalDate periodDate;

    @Column(precision = 18, scale = 2)
    private BigDecimal startingBalance;

    @Column(precision = 18, scale = 2)
    private BigDecimal interest;

    @Column(precision = 18, scale = 2)
    private BigDecimal principal;

    @Column(precision = 18, scale = 2)
    private BigDecimal payment;

    @Column(precision = 18, scale = 2)
    private BigDecimal endingBalance;

    private String status; // PENDING or PAID
}
