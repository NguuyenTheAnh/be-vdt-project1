package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    Page<LoanProduct> findByNameContainingIgnoreCaseAndStatus(String name, LoanProductStatus status, Pageable pageable);

    Page<LoanProduct> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<LoanProduct> findByStatus(LoanProductStatus status, Pageable pageable);
}
