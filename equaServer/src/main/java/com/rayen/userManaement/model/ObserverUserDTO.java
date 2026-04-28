package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ObserverUserDTO extends UserDTO {

    private List<String> permissions;
    private String riskLevel;
    private String kycStatus;
    private String nationalIdNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private LocalDateTime kycSubmittedAt;
    private String kycReviewNote;
}
