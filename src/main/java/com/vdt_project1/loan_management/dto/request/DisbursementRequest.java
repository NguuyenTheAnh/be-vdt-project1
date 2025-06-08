package com.vdt_project1.loan_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisbursementRequest {

    @NotNull(message = "Application ID is required")
    Long applicationId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    Long amount;

    LocalDateTime transactionDate; // Optional, defaults to now

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes;
}