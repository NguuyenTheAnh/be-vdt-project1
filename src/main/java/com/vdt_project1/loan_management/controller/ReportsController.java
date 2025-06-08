package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.response.*;
import com.vdt_project1.loan_management.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("reports")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReportsController {

    LoanApplicationService loanApplicationService;

    /**
     * Thống kê số lượng hồ sơ theo trạng thái
     * Phù hợp để vẽ Bar chart
     */
    @GetMapping("/applications/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<StatusStatisticsResponse>> getApplicationStatisticsByStatus() {
        log.info("Fetching application statistics by status");
        List<StatusStatisticsResponse> statistics = loanApplicationService.getApplicationStatisticsByStatus();
        return ApiResponse.<List<StatusStatisticsResponse>>builder()
                .code(1000)
                .message("Application statistics by status retrieved successfully")
                .data(statistics)
                .build();
    }

    /**
     * Thống kê số lượng hồ sơ theo gói vay
     * Phù hợp để vẽ Bar chart
     */
    @GetMapping("/applications/by-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ProductStatisticsResponse>> getApplicationStatisticsByProduct() {
        log.info("Fetching application statistics by product");
        List<ProductStatisticsResponse> statistics = loanApplicationService.getApplicationStatisticsByProduct();
        return ApiResponse.<List<ProductStatisticsResponse>>builder()
                .code(1000)
                .message("Application statistics by product retrieved successfully")
                .data(statistics)
                .build();
    }

    /**
     * Thống kê tỷ lệ duyệt/từ chối
     * Phù hợp để vẽ Pie chart
     */
    @GetMapping("/applications/approval-ratio")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApprovalRatioResponse> getApprovalRatio() {
        log.info("Fetching approval ratio statistics");
        ApprovalRatioResponse statistics = loanApplicationService.getApprovalRatio();
        return ApiResponse.<ApprovalRatioResponse>builder()
                .code(1000)
                .message("Approval ratio statistics retrieved successfully")
                .data(statistics)
                .build();
    }

    /**
     * Báo cáo tổng số tiền đã duyệt vay theo thời gian
     * Phù hợp để vẽ Line chart
     */
    @GetMapping("/applications/approved-amount-by-time")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ApprovedAmountByTimeResponse>> getApprovedAmountByTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching approved amount statistics from {} to {}", startDate, endDate);

        // Convert LocalDate to LocalDateTime for service method
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<ApprovedAmountByTimeResponse> statistics = loanApplicationService
                .getApprovedAmountByTime(startDateTime, endDateTime);

        return ApiResponse.<List<ApprovedAmountByTimeResponse>>builder()
                .code(1000)
                .message("Approved amount by time statistics retrieved successfully")
                .data(statistics)
                .build();
    }

    /**
     * Lấy tổng quan báo cáo (Dashboard summary)
     */
    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardSummaryResponse> getDashboardSummary() {
        log.info("Fetching dashboard summary");

        // Get approval ratio
        ApprovalRatioResponse approvalRatio = loanApplicationService.getApprovalRatio();

        // Get statistics by status
        List<StatusStatisticsResponse> statusStats = loanApplicationService.getApplicationStatisticsByStatus();

        // Calculate total applications
        Long totalApplications = statusStats.stream()
                .mapToLong(StatusStatisticsResponse::getCount)
                .sum();

        DashboardSummaryResponse summary = DashboardSummaryResponse.builder()
                .totalApplications(totalApplications)
                .approvedCount(approvalRatio.getApprovedCount())
                .rejectedCount(approvalRatio.getRejectedCount())
                .approvalRate(approvalRatio.getApprovalRate())
                .rejectionRate(approvalRatio.getRejectionRate())
                .statusBreakdown(statusStats)
                .build();

        return ApiResponse.<DashboardSummaryResponse>builder()
                .code(1000)
                .message("Dashboard summary retrieved successfully")
                .data(summary)
                .build();
    }
}
