package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String userType;

    public AuthResponseDTO(String token, Long id, String username, String email, String userType) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }
}
