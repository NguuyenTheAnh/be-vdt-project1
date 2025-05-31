package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.LoanProductRequest;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.entity.LoanProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanProductMapper {

    LoanProductMapper INSTANCE = Mappers.getMapper(LoanProductMapper.class);

    LoanProduct toEntity(LoanProductRequest request);

    LoanProductResponse toResponse(LoanProduct entity);

    void updateEntityFromRequest(LoanProductRequest request, @MappingTarget LoanProduct entity);
}
