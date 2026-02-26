package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumTopicRequest {

    /** ID de l'utilisateur qui crée le sujet */
    private Long createdById;
    private String title;
    private String description;
}
