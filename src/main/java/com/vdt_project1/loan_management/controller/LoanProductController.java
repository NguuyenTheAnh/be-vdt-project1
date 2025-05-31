package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("loan-products")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanProductController {

    LoanProductService loanProductService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<LoanProductResponse> createLoanProduct(@Valid @RequestBody LoanProductRequest request) {
        LoanProductResponse response = loanProductService.createLoanProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<LoanProductResponse>> getAllLoanProducts(Pageable pageable) {
        Page<LoanProductResponse> response = loanProductService.getAllLoanProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanProductResponse>> getAllActiveLoanProducts() {
        List<LoanProductResponse> response = loanProductService.getAllActiveLoanProducts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanProductResponse> getLoanProductById(@PathVariable Long id) {
        LoanProductResponse response = loanProductService.getLoanProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<LoanProductResponse> updateLoanProduct(
            @PathVariable Long id,
            @Valid @RequestBody LoanProductRequest request) {
        LoanProductResponse response = loanProductService.updateLoanProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<LoanProductResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam LoanProductStatus status) {
        LoanProductResponse response = loanProductService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
