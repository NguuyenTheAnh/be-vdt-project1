package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.NotificationRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.NotificationResponse;
import com.vdt_project1.loan_management.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.createNotification(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getAllNotifications(Pageable pageable) {
        return ApiResponse.<Page<NotificationResponse>>builder()
                .data(notificationService.getAllNotifications(pageable))
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadNotificationsCount() {
        log.info("Received request to get unread notifications count");
        long count = notificationService.getUnreadNotificationsCount();
        log.info("Unread notifications count: {}", count);
        return ApiResponse.<Long>builder()
                .data(count)
                .build();
    }

    @PostMapping("/mark-all-as-read")
    public ApiResponse<Void> markAllNotificationsAsRead() {
        log.info("Received request to mark all notifications as read");
        notificationService.markAllNotificationsAsRead();
        log.info("Successfully marked all notifications as read");
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.getNotificationById(id))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<NotificationResponse> updateNotification(@PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.updateNotification(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotificationById(id);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
