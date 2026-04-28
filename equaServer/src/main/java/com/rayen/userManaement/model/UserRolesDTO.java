package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesDTO {

    private String userType; // "ASSISTANT" or "OBSERVER"
    private List<String> permissions; // Only for OBSERVER
}
