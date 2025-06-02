package com.vdt_project1.loan_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentRequest {
    @NotNull(message = "Application ID cannot be null")
    Long applicationId;

    @NotNull(message = "Document type cannot be null")
    String documentType;

    @NotNull(message = "File content cannot be null")
    String fileName;
}
