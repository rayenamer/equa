package com.rayen.userManaement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "event_id")
    private Integer eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observer_user_id", nullable = false)
    private ObserverUser performedByObserver;

    /** For API/log display: who performed the action (username from observer). */
    public String getPerformedBy() {
        return performedByObserver != null ? performedByObserver.getUsername() : null;
    }
}
