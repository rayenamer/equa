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
@Table(name = "micro_insurance")
public class MicroInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceId;

    private String type;
    private Double coverageAmount;
    private Double premium;
    private String status; // ACTIVE / INACTIVE
    private LocalDate startDate;
    private LocalDate endDate;

}

