package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String username;
    private String email;
    /** "ASSISTANT" or "OBSERVER" */
    private String userType;
    /** Only for OBSERVER: list of permission codes */
    private List<String> permissions;
}
