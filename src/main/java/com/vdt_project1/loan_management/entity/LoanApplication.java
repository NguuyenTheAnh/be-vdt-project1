package com.vdt_project1.loan_management.entity;

import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
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
@Table(name = "loan_applications")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    LoanProduct loanProduct;

    @Column(name = "requested_amount", nullable = false)
    Long requestedAmount;

    @Column(name = "requested_term", nullable = false)
    Integer requestedTerm;

    @Column(name = "personal_info", nullable = false)
    String personalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 50)
    LoanApplicationStatus status;

    @Column(name = "disbursed_amount")
    Long disbursedAmount;

    @Column(name = "disbursed_date")
    LocalDateTime disbursedDate;

    @Column(name = "internal_notes")
    String internalNotes;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
