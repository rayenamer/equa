package com.rayen.userManaement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @OneToMany(mappedBy = "performedByObserver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();
}
