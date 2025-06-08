package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.LoanApplicationRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.LoanApplicationResponse;
import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("loan-applications")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanApplicationController {
        LoanApplicationService loanApplicationService;

        @PostMapping
        @PreAuthorize("hasAuthority('POST_LOAN_APPLICATIONS_CREATE') or hasRole('ADMIN')")
        public ApiResponse<LoanApplicationResponse> createLoanApplication(
                        @Valid @RequestBody LoanApplicationRequest request) {
                LoanApplicationResponse response = loanApplicationService.createLoanApplication(request);
                return ApiResponse.<LoanApplicationResponse>builder()
                                .data(response)
                                .build();
        }

        @GetMapping
        @PreAuthorize("hasAuthority('GET_LOAN_APPLICATIONS_ALL') or hasRole('ADMIN')")
        public ApiResponse<Page<LoanApplicationResponse>> getAllLoanApplications(Pageable pageable) {
                log.info("Fetching all loan applications with pagination");
                Page<LoanApplicationResponse> response = loanApplicationService.getAllLoanApplications(pageable);
                return ApiResponse.<Page<LoanApplicationResponse>>builder()
                                .data(response)
                                .build();
        }

        @GetMapping("/required-documents/{id}")
        @PreAuthorize("hasAuthority('GET_REQUIRED_DOCUMENTS_BY_LOAN_PRODUCT_ID') or hasRole('ADMIN')")
        public ApiResponse<Map<String, Object>> getRequiredDocuments(@PathVariable Long id) {
                log.info("Fetching required documents for loan product with ID: {}", id);
                Map<String, Object> response = loanApplicationService.getRequiredDocument(id);
                return ApiResponse.<Map<String, Object>>builder()
                                .data(response)
                                .build();
        }

        @GetMapping("/user")
        @PreAuthorize("hasAuthority('GET_LOAN_APPLICATIONS_CURRENT_USER_ALL') or hasRole('ADMIN')")
        public ApiResponse<Page<LoanApplicationResponse>> getMyLoanApplications(Pageable pageable) {
                log.info("Fetching loan applications for the current user");
                Page<LoanApplicationResponse> response = loanApplicationService.getAllLoanApplicationsOfAUser(pageable);
                return ApiResponse.<Page<LoanApplicationResponse>>builder()
                                .data(response)
                                .build();
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('GET_LOAN_APPLICATIONS_BY_ID') or hasRole('ADMIN')")
        public ApiResponse<LoanApplicationResponse> getLoanApplicationById(@PathVariable Long id) {
                log.info("Fetching loan application with ID: {}", id);
                LoanApplicationResponse response = loanApplicationService.getLoanApplicationById(id);
                return ApiResponse.<LoanApplicationResponse>builder()
                                .data(response)
                                .build();
        }

        @PatchMapping("/{id}")
        @PreAuthorize("hasAuthority('PATCH_LOAN_APPLICATIONS_UPDATE_BY_ID') or hasRole('ADMIN')")
        public ApiResponse<LoanApplicationResponse> updateLoanApplication(@PathVariable Long id,
                        @Valid @RequestBody LoanApplicationRequest request) {
                log.info("Updating loan application with ID: {}", id);
                LoanApplicationResponse response = loanApplicationService.updateLoanApplication(id, request);
                return ApiResponse.<LoanApplicationResponse>builder()
                                .data(response)
                                .build();
        }

        // api for user to update their loan application status
        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('PATCH_LOAN_APPLICATIONS_UPDATE_STATUS_BY_ID') or hasRole('ADMIN')")
        public ApiResponse<LoanApplicationResponse> updateLoanApplicationStatus(@PathVariable Long id,
                        @RequestParam LoanApplicationStatus status) {
                log.info("Updating loan application status for ID: {} to {}", id, status);
                LoanApplicationResponse response = loanApplicationService.updateLoanApplicationStatus(id, status);
                return ApiResponse.<LoanApplicationResponse>builder()
                                .data(response)
                                .build();
        }

        // Admin API to update loan application status with internal notes and
        // notifications
        @PatchMapping("/{id}/status/manage")
        @PreAuthorize("hasAuthority('PATCH_LOAN_APPLICATIONS_STATUS_FOR_MANAGE') or hasRole('ADMIN')")
        public ApiResponse<LoanApplicationResponse> updateLoanApplicationStatusForManage(
                        @PathVariable Long id,
                        @RequestParam LoanApplicationStatus status,
                        @RequestParam(required = false) String internal_notes) {
                log.info("Admin updating loan application status for ID: {} to {} with internal notes", id, status);
                LoanApplicationResponse response = loanApplicationService.updateLoanApplicationStatusForManage(id,
                                status, internal_notes);
                return ApiResponse.<LoanApplicationResponse>builder()
                                .data(response)
                                .build();
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('DELETE_LOAN_APPLICATIONS_BY_ID') or hasRole('ADMIN')")
        public ApiResponse<Void> deleteLoanApplicationById(@PathVariable Long id) {
                log.info("Deleting loan application with ID: {}", id);
                loanApplicationService.deleteLoanApplicationById(id);
                return ApiResponse.<Void>builder()
                                .data(null)
                                .build();
        }
}
