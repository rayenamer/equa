package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {

    private Long logId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
    private Integer eventId;
    private Long observerUserId;
}
