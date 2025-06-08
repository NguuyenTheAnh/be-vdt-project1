package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.DisbursementRequest;
import com.vdt_project1.loan_management.dto.request.NotificationRequest;
import com.vdt_project1.loan_management.dto.response.*;
import com.vdt_project1.loan_management.entity.DisbursementTransaction;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.repository.DisbursementTransactionRepository;
import com.vdt_project1.loan_management.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DisbursementTransactionService {
    DisbursementTransactionRepository disbursementTransactionRepository;
    LoanApplicationRepository loanApplicationRepository;
    NotificationService notificationService;
    UserService userService;
    EmailService emailService;

    @Transactional
    public DisbursementResponse createDisbursement(DisbursementRequest request) {
        log.info("Creating disbursement for application ID: {}", request.getApplicationId());
        // Validate loan application exists and is APPROVED or PARTIALLY_DISBURSED
        LoanApplication application = findLoanApplicationById(request.getApplicationId());

        if (application.getStatus() != LoanApplicationStatus.APPROVED &&
                application.getStatus() != LoanApplicationStatus.PARTIALLY_DISBURSED) {
            throw new AppException(ErrorCode.LOAN_APPLICATION_NOT_APPROVED);
        }

        // Validate disbursement amount
        Long totalDisbursed = getTotalDisbursedAmount(request.getApplicationId());
        Long newTotal = totalDisbursed + request.getAmount();

        if (newTotal > application.getRequestedAmount()) {
            throw new AppException(ErrorCode.DISBURSEMENT_AMOUNT_EXCEEDS_APPROVED);
        }

        // Create disbursement transaction
        DisbursementTransaction disbursement = DisbursementTransaction.builder()
                .applicationId(request.getApplicationId())
                .amount(request.getAmount())
                .transactionDate(
                        request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        DisbursementTransaction saved = disbursementTransactionRepository.save(disbursement);

        // Calculate disbursement totals for email
        Long totalDisbursedAfter = newTotal;
        // Send email notification
        try {
            String userEmail = application.getUser().getEmail();
            String userName = application.getUser().getFullName();
            boolean willBeFullyDisbursed = newTotal.equals(application.getRequestedAmount());
            String subject = willBeFullyDisbursed ? "Hoàn tất giải ngân - LoanConv"
                    : "Thông báo giải ngân từng phần - LoanConv";

            String formattedDate = saved.getTransactionDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            emailService.sendDisbursementEmail(
                    userEmail,
                    userName,
                    subject,
                    saved.getAmount().doubleValue(),
                    totalDisbursedAfter.doubleValue(),
                    application.getRequestedAmount().doubleValue(),
                    formattedDate,
                    application.getId(),
                    saved.getNotes());

            log.info("Disbursement email sent successfully to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send disbursement email: {}", e.getMessage());
            // Don't fail the transaction if email fails
        }
        // Check if fully disbursed and update loan application status
        if (newTotal.equals(application.getRequestedAmount())) {
            application.setStatus(LoanApplicationStatus.FULLY_DISBURSED);
            application.setUpdatedAt(LocalDateTime.now());
            loanApplicationRepository.save(application);

            // Send notification for full disbursement
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .applicationId(request.getApplicationId())
                    .message("Your loan has been fully disbursed. Total amount: " + newTotal)
                    .notificationType(com.vdt_project1.loan_management.enums.NotificationType.SYSTEM)
                    .isRead(false)
                    .build();
            notificationService.createNotification(notificationRequest);
        } else {
            // Update status to partially disbursed if not already
            if (application.getStatus() == LoanApplicationStatus.APPROVED) {
                application.setStatus(LoanApplicationStatus.PARTIALLY_DISBURSED);
                application.setUpdatedAt(LocalDateTime.now());
                loanApplicationRepository.save(application);
            }

            // Send notification for partial disbursement
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .applicationId(request.getApplicationId())
                    .message("Disbursement of " + request.getAmount() + " has been processed. Remaining: " +
                            (application.getRequestedAmount() - newTotal))
                    .notificationType(com.vdt_project1.loan_management.enums.NotificationType.SYSTEM)
                    .isRead(false)
                    .build();
            notificationService.createNotification(notificationRequest);
        }

        log.info("Disbursement created successfully with ID: {}", saved.getTransactionId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DisbursementResponse getDisbursementById(Long transactionId) {
        log.info("Fetching disbursement with ID: {}", transactionId);
        DisbursementTransaction disbursement = disbursementTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.DISBURSEMENT_NOT_FOUND));
        return toResponse(disbursement);
    }

    @Transactional(readOnly = true)
    public Page<DisbursementResponse> getAllDisbursements(Pageable pageable) {
        log.info("Fetching all disbursements with pagination");
        return disbursementTransactionRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DisbursementResponse> getDisbursementsByApplication(Long applicationId, Pageable pageable) {
        log.info("Fetching disbursements for application ID: {}", applicationId);
        return disbursementTransactionRepository.findByApplicationIdOrderByTransactionDateDesc(applicationId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DisbursementResponse> getMyDisbursements(Pageable pageable) {
        log.info("Fetching disbursements for current user");
        UserResponse userResponse = userService.getMyProfile();
        return disbursementTransactionRepository.findByUserIdOrderByTransactionDateDesc(userResponse.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DisbursementSummaryResponse getDisbursementSummary(Long applicationId) {
        log.info("Fetching disbursement summary for application ID: {}", applicationId);

        LoanApplication application = findLoanApplicationById(applicationId);
        List<DisbursementTransaction> transactions = disbursementTransactionRepository
                .findByApplicationIdOrderByTransactionDateDesc(applicationId);

        Long totalDisbursed = getTotalDisbursedAmount(applicationId);
        Long remainingAmount = application.getRequestedAmount() - totalDisbursed;

        return DisbursementSummaryResponse.builder()
                .applicationId(applicationId)
                .totalDisbursedAmount(totalDisbursed)
                .requestedAmount(application.getRequestedAmount())
                .remainingAmount(remainingAmount)
                .transactionCount(transactions.size())
                .isFullyDisbursed(remainingAmount == 0)
                .transactions(transactions.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public Long getTotalDisbursedAmount(Long applicationId) {
        return disbursementTransactionRepository.getTotalDisbursedAmount(applicationId);
    }

    @Transactional(readOnly = true)
    public boolean isFullyDisbursed(Long applicationId) {
        LoanApplication application = findLoanApplicationById(applicationId);
        Long totalDisbursed = getTotalDisbursedAmount(applicationId);
        return totalDisbursed.equals(application.getRequestedAmount());
    }

    @Transactional
    public void deleteDisbursement(Long transactionId) {
        log.info("Deleting disbursement with ID: {}", transactionId);
        DisbursementTransaction disbursement = disbursementTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.DISBURSEMENT_NOT_FOUND));

        // Check if loan application status needs to be reverted
        Long applicationId = disbursement.getApplicationId();
        LoanApplication application = findLoanApplicationById(applicationId);

        // Calculate total after deletion
        Long totalAfterDeletion = getTotalDisbursedAmount(applicationId) - disbursement.getAmount();

        // Update status based on remaining amount
        if (application.getStatus() == LoanApplicationStatus.FULLY_DISBURSED ||
                application.getStatus() == LoanApplicationStatus.PARTIALLY_DISBURSED) {

            if (totalAfterDeletion == 0) {
                // No disbursements left, revert to APPROVED
                application.setStatus(LoanApplicationStatus.APPROVED);
            } else if (totalAfterDeletion < application.getRequestedAmount()) {
                // Still has disbursements but not fully disbursed
                application.setStatus(LoanApplicationStatus.PARTIALLY_DISBURSED);
            } else if (totalAfterDeletion.equals(application.getRequestedAmount())) {
                // Still fully disbursed after deletion
                application.setStatus(LoanApplicationStatus.FULLY_DISBURSED);
            }

            application.setUpdatedAt(LocalDateTime.now());
            loanApplicationRepository.save(application);
        }

        disbursementTransactionRepository.delete(disbursement);
        log.info("Disbursement deleted successfully");
    }

    @Transactional(readOnly = true)
    public boolean isUserOwnerOfApplication(Long applicationId) {
        try {
            UserResponse userResponse = userService.getMyProfile();
            LoanApplication application = findLoanApplicationById(applicationId);
            return application.getUser().getId().equals(userResponse.getId());
        } catch (Exception e) {
            return false;
        }
    }

    private LoanApplication findLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_APPLICATION_NOT_FOUND));
    }

    private DisbursementResponse toResponse(DisbursementTransaction disbursement) {
        DisbursementLoanApplicationDto loanAppDto = null;

        if (disbursement.getLoanApplication() != null) {
            LoanApplication loanApp = disbursement.getLoanApplication();
            loanAppDto = DisbursementLoanApplicationDto.builder()
                    .id(loanApp.getId())
                    .status(loanApp.getStatus() != null ? loanApp.getStatus().name() : null)
                    .requestedAmount(loanApp.getRequestedAmount())
                    .userId(loanApp.getUser() != null ? loanApp.getUser().getId() : null)
                    .userEmail(loanApp.getUser() != null ? loanApp.getUser().getEmail() : null)
                    .productId(loanApp.getLoanProduct() != null ? loanApp.getLoanProduct().getId() : null)
                    .productName(loanApp.getLoanProduct() != null ? loanApp.getLoanProduct().getName() : null)
                    .build();
        }

        return DisbursementResponse.builder()
                .transactionId(disbursement.getTransactionId())
                .applicationId(disbursement.getApplicationId())
                .amount(disbursement.getAmount())
                .transactionDate(disbursement.getTransactionDate())
                .notes(disbursement.getNotes())
                .createdAt(disbursement.getCreatedAt())
                .loanApplication(loanAppDto)
                .build();
    }
}
