package com.vdt_project1.loan_management.entity;

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
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "document_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    LoanApplication loanApplication;

    @Column(name = "document_type", nullable = false, length = 50)
    String documentType;

    @Column(name = "file_name", nullable = false, length = 255)
    String fileName;

    @Column(name = "uploaded_at")
    LocalDateTime uploadedAt ;
}
