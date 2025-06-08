package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisbursementLoanApplicationDto {

    Long id;
    String status;
    Long requestedAmount;
    Long userId;
    String userEmail;
    Long productId;
    String productName;
}
