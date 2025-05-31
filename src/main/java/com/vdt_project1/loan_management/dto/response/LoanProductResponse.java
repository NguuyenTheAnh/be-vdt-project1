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
public class LoanProductResponse {
    Long id;
    String name;
    String description;
    Double interestRate;
    Long minAmount;
    Long maxAmount;
    Integer minTerm;
    Integer maxTerm;
    LoanProductStatus status;
    String requiredDocuments;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
