package com.vdt_project1.loan_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class  UserCreationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "Full name is required")
    String fullName;

    @NotBlank(message = "Phone number is required")
    String phoneNumber;

    @NotBlank(message = "Address is required")
    String address;

    String roleName;
}
