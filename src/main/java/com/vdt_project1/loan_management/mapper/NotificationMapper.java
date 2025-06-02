package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.NotificationRequest;
import com.vdt_project1.loan_management.dto.response.NotificationResponse;
import com.vdt_project1.loan_management.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
//    LoanApplication toEntity(LoanApplicationRequest request);
//
//    LoanApplicationResponse toResponse(LoanApplication entity);
//
//    void updateEntityFromRequest(LoanApplicationRequest request, @MappingTarget LoanApplication entity);

    Notification toEntity(NotificationRequest notificationRequest);

    void updateEntityFromRequest(NotificationRequest notificationRequest, @MappingTarget Notification notification);

    NotificationResponse toResponse(Notification notification);
}
