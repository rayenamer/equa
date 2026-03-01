package com.rayen.forumManagement.entity;

import com.rayen.userManaement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private ForumReportTargetType targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ForumTopic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ForumMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ForumReportStatus status = ForumReportStatus.PENDING;

    /**
     * Indique si ce report a déclenché un masquage automatique.
     */
    @Column(name = "auto_hidden", nullable = false)
    private boolean autoHidden = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Column(name = "moderation_note", columnDefinition = "TEXT")
    private String moderationNote;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

