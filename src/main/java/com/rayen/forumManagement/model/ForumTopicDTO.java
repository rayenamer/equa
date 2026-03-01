package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumTopicDTO {

    private Long topicId;
    private String title;
    private String description;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /**
     * Indique si le sujet est masqué (auto-hide ou modération).
     */
    private boolean hidden;
    private List<ForumMessageDTO> messages;
}
