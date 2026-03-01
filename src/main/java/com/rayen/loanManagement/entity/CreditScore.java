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
@Table(name = "credit_scores")
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    private Long userId;
    private Integer score;
    private Integer latePayments;
    private LocalDate lastUpdated;

    public void updateScore() {
        if (latePayments == 0) {
            score += 20;
        } else {
            score -= latePayments * 10;
        }
        lastUpdated = LocalDate.now();
    }

}

