package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.RoleRequest;
import com.vdt_project1.loan_management.dto.response.RoleResponse;
import com.vdt_project1.loan_management.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toRole(RoleRequest roleRequest);

    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toRoleResponse(Role role);
}
