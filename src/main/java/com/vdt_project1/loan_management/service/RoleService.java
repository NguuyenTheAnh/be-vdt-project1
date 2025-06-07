package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.RoleRequest;
import com.vdt_project1.loan_management.dto.response.RoleResponse;
import com.vdt_project1.loan_management.entity.Role;
import com.vdt_project1.loan_management.mapper.RoleMapper;
import com.vdt_project1.loan_management.repository.PermissionRepository;
import com.vdt_project1.loan_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);

        if (roleRequest.getPermissions() != null && !roleRequest.getPermissions().isEmpty()) {
            var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        Page<Role> rolesPage = roleRepository.findAll(pageable);
        return rolesPage.map(roleMapper::toRoleResponse);
    }

    public RoleResponse updateRole(String roleName, RoleRequest roleRequest) {
        Role existingRole = roleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Role updatedRole = roleMapper.toRole(roleRequest);
        updatedRole.setName(existingRole.getName()); // Preserve the original role name

        if (roleRequest.getPermissions() != null && !roleRequest.getPermissions().isEmpty()) {
            var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
            updatedRole.setPermissions(new HashSet<>(permissions));
        }

        return roleMapper.toRoleResponse(roleRepository.save(updatedRole));
    }

    public void deleteRole(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
