package com.rayen.loanManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "micro_insurance")
public class MicroInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceId;

    private String type;
    private Double coverageAmount;
    private Double premium;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
