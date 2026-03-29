package com.rayen.walletManagement.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equa_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private Integer totalSupply;
}