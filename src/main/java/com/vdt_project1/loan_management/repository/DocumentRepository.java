package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Additional query methods can be defined here if needed
    Page<Document> findByLoanApplicationId(Long loanApplicationId, Pageable pageable);
}
