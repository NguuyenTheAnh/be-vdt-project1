package com.vdt_project1.loan_management.dto.request;

import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
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
public class LoanApplicationRequest {

    @NotNull(message = "Product ID is required")
    Long productId;

    @NotNull(message = "Requested amount is required")
    @Min(value = 1, message = "INVALID_LOAN_APPLICATION_AMOUNT")
    Long requestedAmount;

    @NotNull(message = "Requested term is required")
    @Min(value = 1, message = "INVALID_LOAN_APPLICATION_TERM")
    Integer requestedTerm;

    @NotBlank(message = "Personal information is required")
    @Size(min = 10, message = "INVALID_LOAN_APPLICATION_PERSONAL_INFO")
    String personalInfo;

    // Status is optional and will default to NEW if not provided
    LoanApplicationStatus status;

    String disbursedAmount;

    String disbursedDate;

    String internalNotes;

}
