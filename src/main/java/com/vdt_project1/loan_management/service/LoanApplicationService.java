package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.LoanApplicationRequest;
import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.LoanApplicationResponse;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.entity.Document;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.LoanApplicationMapper;
import com.vdt_project1.loan_management.mapper.LoanProductMapper;
import com.vdt_project1.loan_management.repository.DocumentRepository;
import com.vdt_project1.loan_management.repository.LoanApplicationRepository;
import com.vdt_project1.loan_management.repository.LoanProductRepository;
import com.vdt_project1.loan_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Any;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LoanApplicationService {
   LoanApplicationMapper loanApplicationMapper;
   LoanApplicationRepository loanApplicationRepository;
    LoanProductRepository loanProductRepository;
    DocumentRepository documentRepository;
    UserRepository userRepository;
    UserService userService;

    private LoanProduct findLoanProductById(Long id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_PRODUCT_NOT_FOUND));
    }
    private LoanApplication findLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_APPLICATION_NOT_FOUND));
    }
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }
    public static Map<String, Object> buildEmptyDocumentMap(String requiredDocuments) {
        Map<String, Object> map = new HashMap<>();
        if (requiredDocuments != null && !requiredDocuments.isBlank()) {
            String[] keys = requiredDocuments.trim().split("\\s+");
            for (String key : keys) {
                map.put(key, null);
            }
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRequiredDocument(Long applicationId) {
        log.info("Fetching required documents for loan application ID: {}", applicationId);
        LoanApplication loanApplication = findLoanApplicationById(applicationId);
        Map<String, Object> requiredDocs = buildEmptyDocumentMap(loanApplication.getLoanProduct().getRequiredDocuments());
        List<Document> documents = documentRepository.findByLoanApplicationId(applicationId, null).getContent();
        for (Document document : documents) {
            String type = document.getDocumentType();
            String fileName = document.getFileName();
            if (requiredDocs.containsKey(type)) {
                requiredDocs.put(type, fileName);
            }
        }
        return requiredDocs;
    }

    @Transactional
    public LoanApplicationResponse createLoanApplication(LoanApplicationRequest request) {
        UserResponse userResponse = userService.getMyProfile();
       log.info("Creating new loan application for user ID: {}", userResponse.getId());

        LoanApplication loanApplication = loanApplicationMapper.toEntity(request);
        // Validate user exists
        User user = findUserById(userResponse.getId());
        loanApplication.setUser(user);
        // Validate loan product exists
        LoanProduct loanProduct = findLoanProductById(request.getProductId());
        loanApplication.setLoanProduct(loanProduct);

        // Set default status if not provided
        if (loanApplication.getStatus() == null) {
            loanApplication.setStatus(LoanApplicationStatus.NEW);
        }

        // Set timestamps
        loanApplication.setCreatedAt(LocalDateTime.now());
        loanApplication.setUpdatedAt(LocalDateTime.now());

        LoanApplication savedApplication = loanApplicationRepository.save(loanApplication);
        log.info("Loan application created successfully with ID: {}", savedApplication.getId());
        log.info("Loan application status: {}", savedApplication.getStatus());
        return loanApplicationMapper.toResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public Page<LoanApplicationResponse> getAllLoanApplications(Pageable pageable) {
        log.info("Fetching all loan applications with pagination");
        return loanApplicationRepository.findAll(pageable)
                .map(loanApplicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<LoanApplicationResponse> getAllLoanApplicationsOfAUser(Pageable pageable) {
        log.info("Fetching all loan applications for the current user with pagination");
        UserResponse userResponse = userService.getMyProfile();
        return loanApplicationRepository.findByUserId(userResponse.getId(), pageable)
                .map(loanApplicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LoanApplicationResponse getLoanApplicationById(Long id) {
        log.info("Fetching loan application with ID: {}", id);
        LoanApplication loanApplication = findLoanApplicationById(id);
        return loanApplicationMapper.toResponse(loanApplication);
    }

    @Transactional
    public LoanApplicationResponse updateLoanApplication(Long id, LoanApplicationRequest request) {
        log.info("Updating loan application with ID: {}", id);
        LoanApplication existingApplication = findLoanApplicationById(id);

        // Update fields
        loanApplicationMapper.updateEntityFromRequest(request, existingApplication);
        existingApplication.setUpdatedAt(LocalDateTime.now());

        LoanApplication updatedApplication = loanApplicationRepository.save(existingApplication);
        log.info("Loan application updated successfully with ID: {}", updatedApplication.getId());
        return loanApplicationMapper.toResponse(updatedApplication);
    }

    // service for user to update their loan application status
    @Transactional
    public LoanApplicationResponse updateLoanApplicationStatus (Long id, LoanApplicationStatus status) {
        log.info("Updating loan application status for ID: {} to {}", id, status);
        LoanApplication loanApplication = findLoanApplicationById(id);

        // Validate status change
        if (loanApplication.getStatus() == LoanApplicationStatus.REJECTED) {
            throw new AppException(ErrorCode.LOAN_APPLICATION_ALREADY_REJECTED);
        }

        // Update status and timestamps
        loanApplication.setStatus(status);
        loanApplication.setUpdatedAt(LocalDateTime.now());

        LoanApplication updatedApplication = loanApplicationRepository.save(loanApplication);
        log.info("Loan application status updated successfully with ID: {}", updatedApplication.getId());
        return loanApplicationMapper.toResponse(updatedApplication);
    }

    @Transactional
    public void deleteLoanApplicationById(Long id) {
        log.info("Deleting loan application with ID: {}", id);
        LoanApplication loanApplication = findLoanApplicationById(id);
        loanApplicationRepository.delete(loanApplication);
        log.info("Loan application deleted successfully");
    }

}
