package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateForumMessageRequest {

    private String messageText;

    /**
     * Nouvelle URL de GIF (optionnel). Si null, on ne change pas la valeur.
     */
    private String gifUrl;
}

