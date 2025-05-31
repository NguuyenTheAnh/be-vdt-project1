package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
}
