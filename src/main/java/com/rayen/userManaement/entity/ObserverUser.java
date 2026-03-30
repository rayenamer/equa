package com.rayen.userManaement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "observer_users")
@DiscriminatorValue("OBSERVER")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ObserverUser extends User {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "observer_user_permissions", joinColumns = @JoinColumn(name = "observer_user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission", length = 50)
    private List<Permission> permissions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel = RiskLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", length = 20)
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(name = "national_id_number", length = 30)
    private String nationalIdNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "kyc_submitted_at")
    private LocalDateTime kycSubmittedAt;

    @Column(name = "kyc_review_note", length = 500)
    private String kycReviewNote;

    @OneToMany(mappedBy = "performedByObserver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();
}
