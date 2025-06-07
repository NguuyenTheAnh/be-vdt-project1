package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentResponse {
    Long id;
    DocumentLoanApplicationDto loanApplication; // Using simplified DTO to prevent circular references
    String documentType; // e.g., "ID_PROOF", "INCOME_PROOF", etc.
    String fileName; // Name of the uploaded file
    LocalDateTime uploadedAt; // Timestamp of when the document was uploaded, in ISO 8601 format or any other
                              // preferred format
}
