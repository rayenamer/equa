package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "charging_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharginCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "card_code", nullable = false, unique = true)
    private String cardCode;

    @Column(name = "dinar_amount")
    private Integer dinarAmount;

    @Column(name = "consumed")
    private Boolean consumed;

}
