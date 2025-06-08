package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.enums.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class  UserUpdateUserRequest {
    @Email(message = "INVALID_EMAIL")
    String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String fullName;
    String phoneNumber;
    String address;
    AccountStatus accountStatus;
    String roleName;
}
