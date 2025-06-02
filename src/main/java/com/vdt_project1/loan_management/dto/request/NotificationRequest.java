package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {

    @NotNull(message = "Application ID cannot be null")
    Long applicationId;

    @NotNull(message = "Message cannot be null")
    String message;

    @NotNull(message = "Notification type cannot be null")
    NotificationType notificationType;

    Boolean isRead;

}
