package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumMessageRequest {

    /**
     * Contenu du message.
     */
    private String messageText;

    /**
     * URL d'un GIF à afficher avec le message (optionnel).
     */
    private String gifUrl;
}
