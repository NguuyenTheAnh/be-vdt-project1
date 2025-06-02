package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.LoanApplicationRequest;
import com.vdt_project1.loan_management.dto.request.NotificationRequest;
import com.vdt_project1.loan_management.dto.response.LoanApplicationResponse;
import com.vdt_project1.loan_management.dto.response.NotificationResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.entity.Notification;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.NotificationMapper;
import com.vdt_project1.loan_management.repository.LoanApplicationRepository;
import com.vdt_project1.loan_management.repository.NotificationRepository;
import com.vdt_project1.loan_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationMapper notificationMapper;
    NotificationRepository notificationRepository;
    LoanApplicationRepository loanApplicationRepository;
    UserRepository userRepository;
    UserService userService;

    private  LoanApplication findLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_APPLICATION_NOT_FOUND));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request){
        UserResponse userResponse = userService.getMyProfile();

        Notification notification = notificationMapper.toEntity(request);

        if(request.getIsRead() == null) {
            notification.setIsRead(false);
        }
        // set user and loan application
        notification.setLoanApplication( findLoanApplicationById(request.getApplicationId()));
        notification.setUser(findUserById(userResponse.getId()));
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        UserResponse userResponse = userService.getMyProfile();
        log.info("Fetching all notifications for user ID: {}", userResponse.getId());

        return notificationRepository.findByUserId(userResponse.getId(), pageable)
                .map(notification -> {
                    return notificationMapper.toResponse(notification);
                });
    }

    @PostAuthorize("returnObject.user.email == authentication.getName()")
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.info("Fetching notification with ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    // This method is used to mark all notifications as read
    public void markAllNotificationsAsRead() {
        UserResponse userResponse = userService.getMyProfile();
        log.info("Marking all notifications as read for user ID: {}", userResponse.getId());

        Page<Notification> notifications = notificationRepository.findByUserId(userResponse.getId(), Pageable.unpaged());
        if (notifications.hasContent()) {
            notifications.forEach(notification -> {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            });
            log.info("All notifications marked as read for user ID: {}", userResponse.getId());
        } else {
            log.info("No notifications found for user ID: {}", userResponse.getId());
        }
    }

    @Transactional
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        log.info("Updating notification with ID: {}", id);
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // Update fields
        notificationMapper.updateEntityFromRequest(request, existingNotification);

        Notification updatedNotification = notificationRepository.save(existingNotification);
        log.info("Notification updated successfully with ID: {}", updatedNotification.getId());

        return notificationMapper.toResponse(updatedNotification);
    }

    @Transactional
    public void deleteNotificationById(Long id) {
        log.info("Deleting notification with ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
        log.info("Notification deleted successfully");
    }
}
