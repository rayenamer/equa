package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumTopicRequest {

    /**
     * Titre du sujet.
     */
    private String title;

    /**
     * Description / texte initial du sujet.
     */
    private String description;
}
