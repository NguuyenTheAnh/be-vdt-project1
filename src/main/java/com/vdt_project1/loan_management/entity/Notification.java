package com.vdt_project1.loan_management.entity;

import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = true)
    LoanApplication loanApplication;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "is_read", nullable = false)
    Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    NotificationType notificationType;

    @Column(name = "created_at")
    LocalDateTime createdAt;

}
