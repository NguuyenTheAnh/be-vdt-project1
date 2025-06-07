package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.enums.VerificationTokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationTokenRequest {
    @NotNull(message = "User ID cannot be null")
    Long userId;

    @NotNull(message = "Token type cannot be null")
    VerificationTokenType type;

    @NotNull(message = "UUID cannot be null")
    String uuid;

}
