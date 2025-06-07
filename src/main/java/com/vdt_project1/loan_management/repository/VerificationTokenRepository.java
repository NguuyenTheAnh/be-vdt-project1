package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.Notification;
import com.vdt_project1.loan_management.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUuid(String uuid);

    VerificationToken findByUserIdAndType(Long userId, String type);

    VerificationToken findByUserIdAndTypeAndVerified(Long userId, String type, Boolean verified);
}
