package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.DisbursementTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DisbursementTransactionRepository extends JpaRepository<DisbursementTransaction, Long> {

    List<DisbursementTransaction> findByApplicationIdOrderByTransactionDateDesc(Long applicationId);

    Page<DisbursementTransaction> findByApplicationIdOrderByTransactionDateDesc(Long applicationId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(dt.amount), 0) FROM DisbursementTransaction dt WHERE dt.applicationId = :applicationId")
    Long getTotalDisbursedAmount(@Param("applicationId") Long applicationId);

    @Query("SELECT COUNT(dt) FROM DisbursementTransaction dt WHERE dt.applicationId = :applicationId")
    Long countByApplicationId(@Param("applicationId") Long applicationId);

    // Find all disbursements for a specific user's applications
    @Query("SELECT dt FROM DisbursementTransaction dt " +
            "JOIN dt.loanApplication la " +
            "WHERE la.user.id = :userId " +
            "ORDER BY dt.transactionDate DESC")
    Page<DisbursementTransaction> findByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId,
            Pageable pageable);

    // Statistics query for disbursed amount by date range
    @Query("SELECT DATE(dt.transactionDate) as date, SUM(dt.amount) as totalAmount, COUNT(dt) as count " +
           "FROM DisbursementTransaction dt " +
           "WHERE dt.transactionDate >= :startDate AND dt.transactionDate <= :endDate " +
           "GROUP BY DATE(dt.transactionDate) " +
           "ORDER BY DATE(dt.transactionDate)")
    List<Object[]> getDisbursedAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
