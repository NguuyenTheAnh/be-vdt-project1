package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Page<User> findByFullNameContainingIgnoreCaseAndAccountStatus(
            String fullName, AccountStatus accountStatus, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<User> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);
}
