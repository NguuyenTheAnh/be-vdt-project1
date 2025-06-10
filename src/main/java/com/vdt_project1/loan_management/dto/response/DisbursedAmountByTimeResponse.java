package com.vdt_project1.loan_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DisbursedAmountByTimeResponse {
    LocalDate date;
    Long totalDisbursedAmount;
    Long disbursedCount;
}
