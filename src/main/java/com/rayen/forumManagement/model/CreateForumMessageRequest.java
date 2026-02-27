package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumMessageRequest {

    /** ID de l'utilisateur qui envoie le message */
    private Long authorId;
    private String messageText;
}
