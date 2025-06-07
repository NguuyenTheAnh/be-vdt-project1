package com.vdt_project1.loan_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @NotNull(message = "Email cannot be null")
    String email;

    @NotNull(message = "Old password cannot be null")
    String newPassword;

    @NotNull(message = "Token cannot be null")
    String token;
}
