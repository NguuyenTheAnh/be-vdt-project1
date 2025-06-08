package com.vdt_project1.loan_management.dto.response;

import com.vdt_project1.loan_management.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String email;
    String fullName;
    String phoneNumber;
    String address;
    AccountStatus accountStatus;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    RoleDto role;
}
