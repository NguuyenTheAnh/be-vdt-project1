package com.vdt_project1.loan_management.dto.response;


import com.vdt_project1.loan_management.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationTokenResponse {
    Long id;
    VerificationTokenUserDto user;
    String uuid;
    String type;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    Boolean verified;
}
