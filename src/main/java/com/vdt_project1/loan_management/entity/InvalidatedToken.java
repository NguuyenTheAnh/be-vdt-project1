package com.vdt_project1.loan_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invalidated_tokens")
public class InvalidatedToken {
    @Id
    String id;

    @Column(name = "expiration_time", nullable = false)
    Date expirationTime;
}
