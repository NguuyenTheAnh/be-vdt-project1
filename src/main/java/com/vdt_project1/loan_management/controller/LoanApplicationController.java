package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.LoanApplicationRequest;
import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.LoanApplicationResponse;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.service.LoanApplicationService;
import com.vdt_project1.loan_management.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("loan-applications")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanApplicationController {
        LoanApplicationService loanApplicationService;

        @PostMapping
        public ApiResponse<LoanApplicationResponse> createLoanApplication(@Valid @RequestBody LoanApplicationRequest request) {
                LoanApplicationResponse response = loanApplicationService.createLoanApplication(request);
                return ApiResponse.<LoanApplicationResponse>builder()
                        .data(response)
                        .build();
        }

        @GetMapping
        public ApiResponse<Page<LoanApplicationResponse>> getAllLoanApplications(Pageable pageable) {
                log.info("Fetching all loan applications with pagination");
                Page<LoanApplicationResponse> response = loanApplicationService.getAllLoanApplications(pageable);
                return ApiResponse.<Page<LoanApplicationResponse>>builder()
                        .data(response)
                        .build();
        }

        @GetMapping("/required-documents/{id}")
        public ApiResponse<Map<String, Object>> getRequiredDocuments(@PathVariable Long id) {
                log.info("Fetching required documents for loan product with ID: {}", id);
                Map<String, Object> response = loanApplicationService.getRequiredDocument(id);
                return ApiResponse.<Map<String, Object>>builder()
                        .data(response)
                        .build();
        }

        @GetMapping("/user")
        public ApiResponse<Page<LoanApplicationResponse>> getMyLoanApplications(Pageable pageable) {
                log.info("Fetching loan applications for the current user");
                Page<LoanApplicationResponse> response = loanApplicationService.getAllLoanApplicationsOfAUser(pageable);
                return ApiResponse.<Page<LoanApplicationResponse>>builder()
                        .data(response)
                        .build();
        }

        @GetMapping("/{id}")
        public ApiResponse<LoanApplicationResponse> getLoanApplicationById(@PathVariable Long id) {
                log.info("Fetching loan application with ID: {}", id);
                LoanApplicationResponse response = loanApplicationService.getLoanApplicationById(id);
                return ApiResponse.<LoanApplicationResponse>builder()
                        .data(response)
                        .build();
        }

        @PatchMapping("/{id}")
        public ApiResponse<LoanApplicationResponse> updateLoanApplication(@PathVariable Long id, @Valid @RequestBody LoanApplicationRequest request) {
                log.info("Updating loan application with ID: {}", id);
                LoanApplicationResponse response = loanApplicationService.updateLoanApplication(id, request);
                return ApiResponse.<LoanApplicationResponse>builder()
                        .data(response)
                        .build();
        }

        @DeleteMapping("/{id}")
        public ApiResponse<Void> deleteLoanApplicationById(@PathVariable Long id) {
                log.info("Deleting loan application with ID: {}", id);
                loanApplicationService.deleteLoanApplicationById(id);
                return ApiResponse.<Void>builder()
                        .data(null)
                        .build();
        }
}
