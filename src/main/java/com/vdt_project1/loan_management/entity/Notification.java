package com.vdt_project1.loan_management.entity;

import com.vdt_project1.loan_management.enums.LoanProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_products")
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long id;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "interest_rate", nullable = false)
    Double interestRate;

    @Column(name = "min_amount", nullable = false)
    Long minAmount;

    @Column(name = "max_amount", nullable = false)
    Long maxAmount;

    @Column(name = "min_term", nullable = false)
    Integer minTerm;

    @Column(name = "max_term", nullable = false)
    Integer maxTerm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    LoanProductStatus status = LoanProductStatus.ACTIVE;

    @Column(name = "required_documents", nullable = false)
    String requiredDocuments;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
