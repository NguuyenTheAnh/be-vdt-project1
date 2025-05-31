package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.enums.LoanProductStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
     String name;

     String description;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be positive")
    @DecimalMax(value = "100.0", inclusive = true, message = "Interest rate must not exceed 100%")
     Double interestRate;

    @NotNull(message = "Minimum amount is required")
    @Min(value = 0, message = "Minimum amount cannot be negative")
     Long minAmount;

    @NotNull(message = "Maximum amount is required")
    @Min(value = 1, message = "Maximum amount must be positive")
     Long maxAmount;

    @NotNull(message = "Minimum term is required")
    @Min(value = 1, message = "Minimum term must be at least 1")
     Integer minTerm;

    @NotNull(message = "Maximum term is required")
    @Min(value = 1, message = "Maximum term must be at least 1")
     Integer maxTerm;

     LoanProductStatus status;

    @NotNull(message = "Required documents cannot be null")
    @Size(min = 1, message = "At least one required document must be specified")
     Set<String> requiredDocuments;
}
