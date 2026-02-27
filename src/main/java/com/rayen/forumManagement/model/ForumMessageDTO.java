package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumMessageDTO {

    private Long messageId;
    private Long topicId;
    private Long authorId;
    private String authorUsername;
    private String messageText;
    private LocalDateTime createdAt;
}
