package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import com.rayen.userManaement.entity.User;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // The user who created/owns this business
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "industry")
    private String industry;

    @Column(name = "registration_number")
    private String registrationNumber;
}
