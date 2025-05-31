package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.mapper.LoanProductMapper;
import com.vdt_project1.loan_management.repository.LoanProductRepository;
import com.vdt_project1.loan_management.specification.LoanProductSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanProductServiceTest {

    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private LoanProductMapper loanProductMapper;

    @InjectMocks
    private LoanProductService loanProductService;

    private LoanProduct loanProduct1;
    private LoanProduct loanProduct2;
    private LoanProductResponse loanProductResponse1;
    private LoanProductResponse loanProductResponse2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Set up test data
        loanProduct1 = LoanProduct.builder()
                .id(1L)
                .name("Home Loan")
                .description("Loan for home purchase")
                .interestRate(5.5)
                .minAmount(10000L)
                .maxAmount(1000000L)
                .minTerm(12)
                .maxTerm(360)
                .status(LoanProductStatus.ACTIVE)
                .requiredDocuments("ID, Income Proof")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loanProduct2 = LoanProduct.builder()
                .id(2L)
                .name("Car Loan")
                .description("Loan for car purchase")
                .interestRate(7.5)
                .minAmount(5000L)
                .maxAmount(200000L)
                .minTerm(6)
                .maxTerm(84)
                .status(LoanProductStatus.ACTIVE)
                .requiredDocuments("ID, Income Proof, Car Details")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loanProductResponse1 = new LoanProductResponse();
        loanProductResponse1.setId(1L);
        loanProductResponse1.setName("Home Loan");

        loanProductResponse2 = new LoanProductResponse();
        loanProductResponse2.setId(2L);
        loanProductResponse2.setName("Car Loan");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testSearchLoanProducts_WithNameAndStatus() {
        // Arrange
        List<LoanProduct> loanProducts = Arrays.asList(loanProduct1);
        Page<LoanProduct> loanProductPage = new PageImpl<>(loanProducts, pageable, loanProducts.size());

        when(loanProductRepository.findByNameContainingIgnoreCaseAndStatus(eq("Home"), eq(LoanProductStatus.ACTIVE),
                eq(pageable)))
                .thenReturn(loanProductPage);
        when(loanProductMapper.toResponse(eq(loanProduct1))).thenReturn(loanProductResponse1);

        // Act
        Page<LoanProductResponse> result = loanProductService.searchLoanProducts("Home", LoanProductStatus.ACTIVE,
                pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Home Loan", result.getContent().get(0).getName());
    }

    @Test
    void testSearchLoanProducts_WithNameOnly() {
        // Arrange
        List<LoanProduct> loanProducts = Arrays.asList(loanProduct1);
        Page<LoanProduct> loanProductPage = new PageImpl<>(loanProducts, pageable, loanProducts.size());

        when(loanProductRepository.findByNameContainingIgnoreCase(eq("Home"), eq(pageable)))
                .thenReturn(loanProductPage);
        when(loanProductMapper.toResponse(eq(loanProduct1))).thenReturn(loanProductResponse1);

        // Act
        Page<LoanProductResponse> result = loanProductService.searchLoanProducts("Home", null, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Home Loan", result.getContent().get(0).getName());
    }

    @Test
    void testAdvancedSearchLoanProducts() {
        // Arrange
        List<LoanProduct> loanProducts = Arrays.asList(loanProduct1);
        Page<LoanProduct> loanProductPage = new PageImpl<>(loanProducts, pageable, loanProducts.size());

        when(loanProductRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(loanProductPage);
        when(loanProductMapper.toResponse(eq(loanProduct1))).thenReturn(loanProductResponse1);

        // Act
        Page<LoanProductResponse> result = loanProductService.advancedSearchLoanProducts(
                "Home", LoanProductStatus.ACTIVE, 10000L, 1000000L,
                5.0, 6.0, 12, 360, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Home Loan", result.getContent().get(0).getName());
    }
}
