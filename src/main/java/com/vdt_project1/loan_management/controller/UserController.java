package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.UserCreationRequest;
import com.vdt_project1.loan_management.dto.request.UserUpdateRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping()
    @PreAuthorize("hasAuthority('POST_USERS_CREATE') or hasRole('ADMIN')")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createUser(request))
                .build();
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('GET_USERS_ALL') or hasRole('ADMIN')")
    ApiResponse<Page<UserResponse>> getUsers(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User {} is accessing the getUsers endpoint", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<Page<UserResponse>>builder()
                .data(userService.getUsers(name, status, pageable))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_USERS_BY_ID') or hasRole('ADMIN')")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserById(id))
                .build();
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('PATCH_USERS_UPDATE_CURRENT_USER_PROFILE') or hasRole('ADMIN')")
    ApiResponse<UserResponse> updateUserProfile(@RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateMyProfile(request))
                .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PATCH_USERS_UPDATE_BY_ID') or hasRole('ADMIN')")
    ApiResponse<UserResponse> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USERS_BY_ID') or hasRole('ADMIN')")
    ApiResponse<Void> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
