package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.enums.LoanApplicationStatus;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
        // Page<LoanProduct> findByNameContainingIgnoreCaseAndStatus(String name,
        // LoanProductStatus status, Pageable pageable);
        //
        // Page<LoanProduct> findByNameContainingIgnoreCase(String name, Pageable
        // pageable);
        //
        // Page<LoanProduct> findByStatus(LoanProductStatus status, Pageable pageable);

        Page<LoanApplication> findByUserId(Long userId, Pageable pageable);

        Page<LoanApplication> findByStatus(LoanProductStatus status, Pageable pageable);

        Page<LoanApplication> findByLoanProduct(LoanProduct loanProduct, Pageable pageable);

        // Statistics queries
        @Query("SELECT la.status as status, COUNT(la) as count FROM LoanApplication la GROUP BY la.status")
        List<Object[]> getApplicationCountByStatus();

        @Query("SELECT lp.name as productName, COUNT(la) as count FROM LoanApplication la " +
                        "JOIN la.loanProduct lp GROUP BY lp.name")
        List<Object[]> getApplicationCountByProduct();

        @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = :status")
        Long countByStatus(@Param("status") LoanApplicationStatus status);

        @Query("SELECT DATE(la.createdAt) as date, SUM(la.requestedAmount) as totalAmount, COUNT(la) as count " +
                        "FROM LoanApplication la " +
                        "WHERE la.status = 'APPROVED' OR la.status = 'PARTIALLY_DISBURSED' OR la.status = 'FULLY_DISBURSED' AND la.createdAt >= :startDate AND la.createdAt <= :endDate "
                        +
                        "GROUP BY DATE(la.createdAt) " +
                        "ORDER BY DATE(la.createdAt)")
        List<Object[]> getApprovedAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
