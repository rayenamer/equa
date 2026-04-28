package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycProfileDTO {
    private String nationalIdNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private LocalDateTime kycSubmittedAt;
    private String kycReviewNote;
}
