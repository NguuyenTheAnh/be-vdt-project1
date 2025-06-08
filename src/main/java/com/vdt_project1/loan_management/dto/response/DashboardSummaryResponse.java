package com.vdt_project1.loan_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO cho tổng quan báo cáo (Dashboard Summary)
 * Chứa thông tin tổng hợp cho dashboard admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DashboardSummaryResponse {

    /**
     * Tổng số hồ sơ vay
     */
    Long totalApplications;

    /**
     * Số hồ sơ đã được duyệt
     */
    Long approvedCount;

    /**
     * Số hồ sơ bị từ chối
     */
    Long rejectedCount;

    /**
     * Tỷ lệ duyệt (%)
     */
    Double approvalRate;

    /**
     * Tỷ lệ từ chối (%)
     */
    Double rejectionRate;

    /**
     * Chi tiết thống kê theo từng trạng thái
     */
    List<StatusStatisticsResponse> statusBreakdown;
}
