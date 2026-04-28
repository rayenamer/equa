package com.rayen.forumManagement.model;

import com.rayen.forumManagement.entity.ForumReportStatus;
import com.rayen.forumManagement.entity.ForumReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumReportDTO {

    private Long reportId;
    private ForumReportTargetType targetType;
    private Long topicId;
    private Long messageId;
    private Long reporterId;
    private String reporterUsername;
    private String reason;
    private ForumReportStatus status;
    private boolean autoHidden;
    private LocalDateTime createdAt;
    private Long moderatorId;
    private String moderatorUsername;
    private LocalDateTime moderatedAt;
    private String moderationNote;
}

