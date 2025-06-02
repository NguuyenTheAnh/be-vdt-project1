package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.LoanProductMapper;
import com.vdt_project1.loan_management.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanProductService {
    LoanProductRepository loanProductRepository;
    LoanProductMapper loanProductMapper;

    private LoanProduct findLoanProductById(Long id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_PRODUCT_NOT_FOUND));
    }

    private void validateLoanProduct(LoanProduct loanProduct) {
        // Validate min amount is less than max amount
        if (loanProduct.getMinAmount() >= loanProduct.getMaxAmount()) {
            throw new AppException(ErrorCode.INVALID_LOAN_PRODUCT_AMOUNT_RANGE);
        }

        // Validate min term is less than max term
        if (loanProduct.getMinTerm() >= loanProduct.getMaxTerm()) {
            throw new AppException(ErrorCode.INVALID_LOAN_PRODUCT_TERM_RANGE);
        }
    }

    @Transactional
    public LoanProductResponse createLoanProduct(LoanProductRequest request) {
        log.info("Creating new loan product: {}", request.getName());

        // Validate required documents before creating entity
        if (request.getRequiredDocuments() == null || request.getRequiredDocuments().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_LOAN_PRODUCT_DOCUMENTS);
        }

        LoanProduct loanProduct = loanProductMapper.toEntity(request);

        if (loanProduct.getStatus() == null) {
            loanProduct.setStatus(LoanProductStatus.ACTIVE);
        }

        log.info("Required documents: {}", request.getRequiredDocuments());

        loanProduct.setCreatedAt(LocalDateTime.now());
        loanProduct.setUpdatedAt(LocalDateTime.now());

        validateLoanProduct(loanProduct);

        LoanProduct savedProduct = loanProductRepository.save(loanProduct);
        log.info("Loan product created successfully with ID: {}", savedProduct.getId());

        return loanProductMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public LoanProductResponse getLoanProductById(Long id) {
        log.info("Fetching loan product with ID: {}", id);
        LoanProduct loanProduct = findLoanProductById(id);
        return loanProductMapper.toResponse(loanProduct);
    }

    @Transactional
    public LoanProductResponse updateLoanProduct(Long id, LoanProductRequest request) {
        log.info("Updating loan product with ID: {}", id);

        // Validate required documents before updating
        if (request.getRequiredDocuments() == null || request.getRequiredDocuments().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_LOAN_PRODUCT_DOCUMENTS);
        }

        LoanProduct existingProduct = findLoanProductById(id);

        loanProductMapper.updateEntityFromRequest(request, existingProduct);
        existingProduct.setUpdatedAt(LocalDateTime.now());

        validateLoanProduct(existingProduct);

        LoanProduct updatedProduct = loanProductRepository.save(existingProduct);
        log.info("Loan product updated successfully: {}", updatedProduct.getId());

        return loanProductMapper.toResponse(updatedProduct);
    }

    @Transactional
    public LoanProductResponse changeStatus(Long id, LoanProductStatus status) {
        log.info("Changing status of loan product with ID: {} to {}", id, status);
        LoanProduct loanProduct = findLoanProductById(id);

        loanProduct.setStatus(status);
        loanProduct.setUpdatedAt(LocalDateTime.now());

        LoanProduct updatedProduct = loanProductRepository.save(loanProduct);
        log.info("Loan product status changed successfully: {}", updatedProduct.getId());

        return loanProductMapper.toResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public Page<LoanProductResponse> getAllLoanProducts(String name, String status, Pageable pageable) {
        Page<LoanProduct> loanProductPage;
        if(name!=null && status !=null){
            loanProductPage = loanProductRepository.findByNameContainingIgnoreCaseAndStatus(
                    name, LoanProductStatus.valueOf(status.toUpperCase()), pageable);
        }
        else if(name != null) {
            loanProductPage = loanProductRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if(status != null) {
            loanProductPage = loanProductRepository.findByStatus(LoanProductStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            loanProductPage = loanProductRepository.findAll(pageable);
        }
        return loanProductPage.map(loanProductMapper::toResponse);
    }
}
