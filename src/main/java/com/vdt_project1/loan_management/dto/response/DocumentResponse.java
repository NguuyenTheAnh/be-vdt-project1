package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// CREATE TABLE "documents" (
// "document_id" bigserial PRIMARY KEY,
// "application_id" bigint NOT NULL,
// "document_type" varchar(50) NOT NULL,
// "file_name" varchar(255) NOT NULL,
// "uploaded_at" timestamp without time zone
// );
public class DocumentResponse {
    Long id;
    DocumentLoanApplicationDto loanApplication; // Using simplified DTO to prevent circular references
    String documentType; // e.g., "ID_PROOF", "INCOME_PROOF", etc.
    String fileName; // Name of the uploaded file
    LocalDateTime uploadedAt; // Timestamp of when the document was uploaded, in ISO 8601 format or any other
                              // preferred format
}
