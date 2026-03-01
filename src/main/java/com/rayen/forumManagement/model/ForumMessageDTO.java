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
    /**
     * URL d'un GIF associé au message (optionnel).
     */
    private String gifUrl;
    /**
     * Indique si le message est masqué (auto-hide ou modération).
     */
    private boolean hidden;
}
