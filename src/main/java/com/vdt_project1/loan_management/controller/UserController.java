package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.UserCreationRequest;
import com.vdt_project1.loan_management.dto.request.UserUpdateRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createUser(request))
                .build();
    }

    @GetMapping()
    ApiResponse<Page<UserResponse>> getUsers(Pageable pageable) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User {} is accessing the getUsers endpoint", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<Page<UserResponse>>builder()
                .data(userService.getUsers(pageable))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserById(id))
                .build();
    }

    @PatchMapping
    ApiResponse<UserResponse> updateUserProfile(@RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateMyProfile(request))
                .build();
    }

    @PatchMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
