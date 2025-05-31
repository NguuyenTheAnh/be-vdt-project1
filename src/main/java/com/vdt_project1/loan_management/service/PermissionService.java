package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.PermissionRequest;
import com.vdt_project1.loan_management.dto.response.PermissionResponse;
import com.vdt_project1.loan_management.entity.Permission;
import com.vdt_project1.loan_management.mapper.PermissionMapper;
import com.vdt_project1.loan_management.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        Permission permission = permissionMapper.toPermission(permissionRequest);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        return permissions.map(permissionMapper::toPermissionResponse);
    }

    public void deletePermission(String permissionName) {
        permissionRepository.deleteById(permissionName);
    }
}
