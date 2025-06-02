package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);

//    countByUserIdAndIsReadFalse
    long countByUserIdAndIsReadFalse(Long userId);

}
