package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisbursementSummaryResponse {

    Long applicationId;
    Long totalDisbursedAmount;
    Long requestedAmount;
    Long remainingAmount;
    Integer transactionCount;
    Boolean isFullyDisbursed;
    List<DisbursementResponse> transactions;
}
