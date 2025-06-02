package com.vdt_project1.loan_management.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    String message;
    Boolean isRead;
    String notificationType; // e.g., "APPLICATION_STATUS_UPDATE", "LOAN_APPROVAL", etc.
    String createdAt; // ISO 8601 format or any other preferred format

    UserResponse user; // Optional, if you want to include user details
    LoanApplicationResponse loanApplication; // Optional, if you want to include application details
}
