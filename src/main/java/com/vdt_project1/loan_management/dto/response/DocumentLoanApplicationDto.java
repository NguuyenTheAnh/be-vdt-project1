package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * A simplified DTO for LoanApplication to be used within DocumentResponse.
 * This prevents circular references during JSON serialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentLoanApplicationDto {
    Long id;
    String status;
    Long userId;
    String userEmail; // Basic user info
    Long productId;
    String productName; // Basic product info
}
