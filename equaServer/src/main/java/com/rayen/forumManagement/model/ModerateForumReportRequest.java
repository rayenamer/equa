package com.rayen.forumManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerateForumReportRequest {

    /**
     * Action de modération : "HIDE" pour masquer définitivement, "IGNORE" pour rejeter.
     */
    private String action;

    /**
     * Note libre pour l'historique de modération.
     */
    private String note;
}

