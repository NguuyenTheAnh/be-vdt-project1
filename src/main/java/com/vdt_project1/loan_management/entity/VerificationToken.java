package com.vdt_project1.loan_management.entity;

import com.vdt_project1.loan_management.enums.VerificationTokenType;
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
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "uuid", nullable = false, unique = true, length = 255)
    String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    VerificationTokenType type;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @Column(name = "verified", nullable = false)
    Boolean verified;

}
