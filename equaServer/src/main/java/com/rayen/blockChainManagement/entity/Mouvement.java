package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mouvements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mouvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String libelle;

    @Column(name = "mouvement_type")
    private String type; // 'sortant' | 'entrant'

    private String compte;
    private Double montant;
    private String categorie;
    private String statut; // 'valide' | 'en_attente' | 'annule'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;
}
