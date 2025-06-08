package com.vdt_project1.loan_management.dto.response;

import com.vdt_project1.loan_management.enums.LoanProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanApplicationResponse {
    Long id;
    Long requestedAmount;
    Integer requestedTerm;
    String personalInfo;
    String status; // LoanApplicationStatus
    String internalNotes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    LoanProductResponse loanProduct; // Optional, if you want to include product details
    UserResponse user; // Optional, if you want to include user details
}
