package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.PermissionRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.PermissionResponse;
import com.vdt_project1.loan_management.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("permissions")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .data(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<PermissionResponse>> getPermissions(Pageable pageable) {
        return ApiResponse.<Page<PermissionResponse>>builder()
                .data(permissionService.getAllPermissions(pageable))
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> deletePermission(@PathVariable String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
