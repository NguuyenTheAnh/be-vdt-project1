package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.enums.LoanProductStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatusCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "INVALID_LOAN_PRODUCT_NAME")
    String name;

    String description;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_LOAN_PRODUCT_MIN_INTEREST_RATE")
    @DecimalMax(value = "100.0", message = "INVALID_LOAN_PRODUCT_MAX_INTEREST_RATE")
    Double interestRate;

    @NotNull(message = "Minimum amount is required")
    @Min(value = 0, message = "INVALID_LOAN_PRODUCT_MIN_AMOUNT")
    Long minAmount;

    @NotNull(message = "Maximum amount is required")
    @Min(value = 1, message = "INVALID_LOAN_PRODUCT_MAX_AMOUNT")
    Long maxAmount;

    @NotNull(message = "Minimum term is required")
    @Min(value = 1, message = "INVALID_LOAN_PRODUCT_MIN_TERM")
    Integer minTerm;

    @NotNull(message = "Maximum term is required")
    @Min(value = 1, message = "INVALID_LOAN_PRODUCT_MAX_TERM")
    Integer maxTerm;

    LoanProductStatus status;

    @NotBlank(message = "Required documents cannot be blank")
    String requiredDocuments;
}
