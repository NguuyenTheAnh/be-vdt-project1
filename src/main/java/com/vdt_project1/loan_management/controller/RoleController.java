package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.RoleRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.RoleResponse;
import com.vdt_project1.loan_management.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.createRole(roleRequest))
                .build();
    }

    @GetMapping
    ApiResponse<Page<RoleResponse>> getAllRoles(Pageable pageable) {
        return ApiResponse.<Page<RoleResponse>>builder()
                .data   (roleService.getAllRoles(pageable))
                .build();
    }

    @PatchMapping("/{roleName}")
    ApiResponse<RoleResponse> updateRole(@PathVariable("roleName") String roleName,
            @RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.updateRole(roleName, roleRequest))
                .build();
    }

    @DeleteMapping("/{roleName}")
    ApiResponse<Void> deleteRole(@PathVariable("roleName") String roleName) {
        roleService.deleteRole(roleName);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
