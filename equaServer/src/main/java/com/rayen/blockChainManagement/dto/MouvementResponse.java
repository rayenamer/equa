package com.rayen.blockChainManagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MouvementResponse {
    private Long id;
    private LocalDate date;
    private String libelle;
    private String type;
    private String compte;
    private Double montant;
    private String categorie;
    private String statut;
}
