package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.service.LoanProductService;
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
@RequestMapping("loan-products")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanProductController {

        LoanProductService loanProductService;

        @PostMapping
        @PreAuthorize("hasAuthority('POST_LOAN_PRODUCTS_CREATE')")
        public ApiResponse<LoanProductResponse> createLoanProduct(@Valid @RequestBody LoanProductRequest request) {
                log.info("Creating new loan product: {}", request.getName());
                return ApiResponse.<LoanProductResponse>builder()
                                .data(loanProductService.createLoanProduct(request))
                                .build();
        }

        @GetMapping
        @PreAuthorize("hasAuthority('GET_LOAN_PRODUCTS_ALL')")
        public ApiResponse<Page<LoanProductResponse>> getAllLoanProducts(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String status,
                        Pageable pageable) {
                log.info("Fetching all loan products with pagination");
                return ApiResponse.<Page<LoanProductResponse>>builder()
                                .data(loanProductService.getAllLoanProducts(name, status, pageable))
                                .build();
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('GET_LOAN_PRODUCTS_BY_ID')")
        public ApiResponse<LoanProductResponse> getLoanProductById(@PathVariable Long id) {
                log.info("Fetching loan product with ID: {}", id);
                return ApiResponse.<LoanProductResponse>builder()
                                .data(loanProductService.getLoanProductById(id))
                                .build();
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('PUT_LOAN_PRODUCTS_UPDATE_BY_ID')")
        public ApiResponse<LoanProductResponse> updateLoanProduct(
                        @PathVariable Long id,
                        @Valid @RequestBody LoanProductRequest request) {
                log.info("Updating loan product with ID: {}", id);
                return ApiResponse.<LoanProductResponse>builder()
                                .data(loanProductService.updateLoanProduct(id, request))
                                .build();
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('PATCH_LOAN_PRODUCTS_UPDATE_STATUS_BY_ID')")
        public ApiResponse<LoanProductResponse> changeStatus(
                        @PathVariable Long id,
                        @RequestParam LoanProductStatus status) {
                log.info("Changing status of loan product with ID: {} to {}", id, status);
                return ApiResponse.<LoanProductResponse>builder()
                                .data(loanProductService.changeStatus(id, status))
                                .build();
        }
}
