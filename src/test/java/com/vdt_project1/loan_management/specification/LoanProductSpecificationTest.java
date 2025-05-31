package com.vdt_project1.loan_management.specification;

import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanProductSpecificationTest {

    @Mock
    private Root<LoanProduct> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<LoanProductStatus> statusPath;

    @Mock
    private Path<Long> minAmountPath;

    @Mock
    private Path<Long> maxAmountPath;

    @Mock
    private Path<Double> interestRatePath;

    @Mock
    private Path<Integer> minTermPath;

    @Mock
    private Path<Integer> maxTermPath;

    @Mock
    private Predicate predicate;

    @Test
    void testSearchLoanProducts_WithAllParams() {
        // Arrange
        String name = "Test";
        LoanProductStatus status = LoanProductStatus.ACTIVE;
        Long minAmount = 10000L;
        Long maxAmount = 100000L;
        Double minInterestRate = 5.0;
        Double maxInterestRate = 10.0;
        Integer minTerm = 12;
        Integer maxTerm = 60;

        // Mock behavior
        when(root.get("name")).thenReturn(namePath);
        when(root.get("status")).thenReturn(statusPath);
        when(root.get("minAmount")).thenReturn(minAmountPath);
        when(root.get("maxAmount")).thenReturn(maxAmountPath);
        when(root.get("interestRate")).thenReturn(interestRatePath);
        when(root.get("minTerm")).thenReturn(minTermPath);
        when(root.get("maxTerm")).thenReturn(maxTermPath);

        when(criteriaBuilder.like(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.greaterThanOrEqualTo(any(Expression.class), any(Long.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Expression.class), any(Long.class))).thenReturn(predicate);
        when(criteriaBuilder.greaterThanOrEqualTo(any(Expression.class), any(Double.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Expression.class), any(Double.class))).thenReturn(predicate);
        when(criteriaBuilder.greaterThanOrEqualTo(any(Expression.class), any(Integer.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Expression.class), any(Integer.class))).thenReturn(predicate);
        when(criteriaBuilder.and(any())).thenReturn(predicate);
        when(criteriaBuilder.lower(any())).thenReturn(namePath);

        // Act
        Specification<LoanProduct> specification = LoanProductSpecification.searchLoanProducts(
                name, status, minAmount, maxAmount, minInterestRate, maxInterestRate, minTerm, maxTerm);

        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(result);
        verify(criteriaBuilder, times(1)).like(any(), eq("%" + name.toLowerCase() + "%"));
        verify(criteriaBuilder, times(1)).equal(statusPath, status);
        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(minAmountPath, minAmount);
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(maxAmountPath, maxAmount);
        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(interestRatePath, minInterestRate);
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(interestRatePath, maxInterestRate);
        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(minTermPath, minTerm);
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(maxTermPath, maxTerm);
    }

    @Test
    void testSearchLoanProducts_WithOnlyName() {
        // Arrange
        String name = "Test";

        // Mock behavior
        when(root.get("name")).thenReturn(namePath);
        when(criteriaBuilder.like(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.and(any())).thenReturn(predicate);
        when(criteriaBuilder.lower(any())).thenReturn(namePath);

        // Act
        Specification<LoanProduct> specification = LoanProductSpecification.searchLoanProducts(
                name, null, null, null, null, null, null, null);

        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(result);
        verify(criteriaBuilder, times(1)).like(any(), eq("%" + name.toLowerCase() + "%"));
        verify(root, never()).get("status");
        verify(root, never()).get("minAmount");
        verify(root, never()).get("maxAmount");
        verify(root, never()).get("interestRate");
        verify(root, never()).get("minTerm");
        verify(root, never()).get("maxTerm");
    }
}
