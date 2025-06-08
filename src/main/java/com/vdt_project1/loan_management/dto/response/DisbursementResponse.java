package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisbursementResponse {

    Long transactionId;
    Long applicationId;
    Long amount;
    LocalDateTime transactionDate;
    String notes;
    LocalDateTime createdAt;

    // Basic loan application info to avoid circular references
    DisbursementLoanApplicationDto loanApplication;
}
