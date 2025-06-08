package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.UserCreationRequest;
import com.vdt_project1.loan_management.dto.request.UserUpdateRequest;
import com.vdt_project1.loan_management.dto.response.LoanProductResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.entity.LoanProduct;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.enums.AccountStatus;
import com.vdt_project1.loan_management.enums.LoanProductStatus;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.UserMapper;
import com.vdt_project1.loan_management.repository.RoleRepository;
import com.vdt_project1.loan_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        boolean userExists = userRepository.existsByEmail(request.getEmail());
        if (userExists) {
            log.warn("User with email '{}' already exists", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(AccountStatus.INACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        String roleName = request.getRoleName() != null ? request.getRoleName() : "USER";
        try {
            user.setRole(roleRepository.findById(roleName)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST)));
        } catch (Exception e) {
            log.error("Error setting user role: {}", e.getMessage());
            throw e;
        }

        log.info("Saving user to database");
        User savedUser = userRepository.save(user);
        log.info("User saved with ID: {}", savedUser.getId());

        return userMapper.toUserResponse(savedUser);
    }

    public Page<UserResponse> getUsers(String name, String status, Pageable pageable) {
        Page<User> userPage;
        if (name != null && status != null) {
            userPage = userRepository.findByFullNameContainingIgnoreCaseAndAccountStatus(name, AccountStatus.valueOf(status.toUpperCase()), pageable);
        } else if (name != null) {
            userPage = userRepository.findByFullNameContainingIgnoreCase(name, pageable);
        } else if (status != null) {
            userPage = userRepository.findByAccountStatus(AccountStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(userMapper::toUserResponse);
    }

    public UserResponse getMyProfile() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateMyProfile(UserUpdateRequest request) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        userMapper.updateProfile(user, request);
        user.setUpdatedAt(LocalDateTime.now());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleName() != null && !request.getRoleName().isEmpty()) {
            user.setRole(roleRepository.findById(request.getRoleName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST)));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        if (request.getRoleName() != null && !request.getRoleName().isEmpty()) {
            user.setRole(roleRepository.findById(request.getRoleName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST)));
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
