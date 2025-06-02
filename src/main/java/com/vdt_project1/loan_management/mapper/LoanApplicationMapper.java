package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.LoanApplicationRequest;
import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.LoanApplicationResponse;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.entity.LoanProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    LoanApplication toEntity(LoanApplicationRequest request);

    LoanApplicationResponse toResponse(LoanApplication entity);

    void updateEntityFromRequest(LoanApplicationRequest request, @MappingTarget LoanApplication entity);
}
