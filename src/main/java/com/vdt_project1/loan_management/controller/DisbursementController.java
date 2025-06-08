package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.DisbursementRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.DisbursementResponse;
import com.vdt_project1.loan_management.dto.response.DisbursementSummaryResponse;
import com.vdt_project1.loan_management.service.DisbursementTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("disbursements")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DisbursementController {

    DisbursementTransactionService disbursementTransactionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DisbursementResponse> createDisbursement(
            @Valid @RequestBody DisbursementRequest request) {
        log.info("Creating disbursement for application ID: {}", request.getApplicationId());
        DisbursementResponse response = disbursementTransactionService.createDisbursement(request);
        return ApiResponse.<DisbursementResponse>builder()
                .code(1000)
                .message("Disbursement created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DisbursementResponse> getDisbursementById(@PathVariable Long transactionId) {
        log.info("Fetching disbursement with ID: {}", transactionId);
        DisbursementResponse response = disbursementTransactionService.getDisbursementById(transactionId);
        return ApiResponse.<DisbursementResponse>builder()
                .code(1000)
                .data(response)
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<DisbursementResponse>> getAllDisbursements(Pageable pageable) {
        log.info("Fetching all disbursements with pagination");
        Page<DisbursementResponse> response = disbursementTransactionService.getAllDisbursements(pageable);
        return ApiResponse.<Page<DisbursementResponse>>builder()
                .code(1000)
                .data(response)
                .build();
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @disbursementTransactionService.isUserOwnerOfApplication(#applicationId))")
    public ApiResponse<Page<DisbursementResponse>> getDisbursementsByApplication(
            @PathVariable Long applicationId, Pageable pageable) {
        log.info("Fetching disbursements for application ID: {}", applicationId);
        Page<DisbursementResponse> response = disbursementTransactionService
                .getDisbursementsByApplication(applicationId, pageable);
        return ApiResponse.<Page<DisbursementResponse>>builder()
                .code(1000)
                .data(response)
                .build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<Page<DisbursementResponse>> getMyDisbursements(Pageable pageable) {
        log.info("Fetching disbursements for current user");
        Page<DisbursementResponse> response = disbursementTransactionService.getMyDisbursements(pageable);
        return ApiResponse.<Page<DisbursementResponse>>builder()
                .code(1000)
                .data(response)
                .build();
    }

    @GetMapping("/application/{applicationId}/summary")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @disbursementTransactionService.isUserOwnerOfApplication(#applicationId))")
    public ApiResponse<DisbursementSummaryResponse> getDisbursementSummary(
            @PathVariable Long applicationId) {
        log.info("Fetching disbursement summary for application ID: {}", applicationId);
        DisbursementSummaryResponse response = disbursementTransactionService
                .getDisbursementSummary(applicationId);
        return ApiResponse.<DisbursementSummaryResponse>builder()
                .code(1000)
                .data(response)
                .build();
    }

    @DeleteMapping("/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteDisbursement(@PathVariable Long transactionId) {
        log.info("Deleting disbursement with ID: {}", transactionId);
        disbursementTransactionService.deleteDisbursement(transactionId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Disbursement deleted successfully")
                .data(null)
                .build();
    }
}
