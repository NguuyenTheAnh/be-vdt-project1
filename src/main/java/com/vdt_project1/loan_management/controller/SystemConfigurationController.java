package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.SystemConfigurationRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.SystemConfigurationResponse;
import com.vdt_project1.loan_management.service.SystemConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("system-configurations")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SystemConfigurationController {
    
    SystemConfigurationService systemConfigurationService;
    
    /**
     * Create a new system configuration
     * Only accessible by ADMIN users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigurationResponse> createConfiguration(
            @Valid @RequestBody SystemConfigurationRequest request) {
        log.info("Request to create system configuration with key: {}", request.getConfigKey());
        
        SystemConfigurationResponse response = systemConfigurationService.createConfiguration(request);
        
        return ApiResponse.<SystemConfigurationResponse>builder()
                .code(1000)
                .message("System configuration created successfully")
                .data(response)
                .build();
    }
    
    /**
     * Get all system configurations
     * Only accessible by ADMIN users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SystemConfigurationResponse>> getAllConfigurations() {
        log.info("Request to get all system configurations");
        
        List<SystemConfigurationResponse> response = systemConfigurationService.getAllConfigurations();
        
        return ApiResponse.<List<SystemConfigurationResponse>>builder()
                .code(1000)
                .message("System configurations retrieved successfully")
                .data(response)
                .build();
    }
    
    /**
     * Get system configuration by ID
     * Only accessible by ADMIN users
     */
    @GetMapping("/{configId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigurationResponse> getConfigurationById(
            @PathVariable Long configId) {
        log.info("Request to get system configuration with ID: {}", configId);
        
        SystemConfigurationResponse response = systemConfigurationService.getConfigurationById(configId);
        
        return ApiResponse.<SystemConfigurationResponse>builder()
                .code(1000)
                .message("System configuration retrieved successfully")
                .data(response)
                .build();
    }
    
    /**
     * Get system configuration by key
     * Only accessible by ADMIN users
     */
    @GetMapping("/key/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigurationResponse> getConfigurationByKey(
            @PathVariable String configKey) {
        log.info("Request to get system configuration with key: {}", configKey);
        
        SystemConfigurationResponse response = systemConfigurationService.getConfigurationByKey(configKey);
        
        return ApiResponse.<SystemConfigurationResponse>builder()
                .code(1000)
                .message("System configuration retrieved successfully")
                .data(response)
                .build();
    }
    
    /**
     * Update system configuration
     * Only accessible by ADMIN users
     */
    @PutMapping("/{configId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigurationResponse> updateConfiguration(
            @PathVariable Long configId,
            @Valid @RequestBody SystemConfigurationRequest request) {
        log.info("Request to update system configuration with ID: {}", configId);
        
        SystemConfigurationResponse response = systemConfigurationService.updateConfiguration(configId, request);
        
        return ApiResponse.<SystemConfigurationResponse>builder()
                .code(1000)
                .message("System configuration updated successfully")
                .data(response)
                .build();
    }
    
    /**
     * Delete system configuration
     * Only accessible by ADMIN users
     */
    @DeleteMapping("/{configId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteConfiguration(@PathVariable Long configId) {
        log.info("Request to delete system configuration with ID: {}", configId);
        
        systemConfigurationService.deleteConfiguration(configId);
        
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("System configuration deleted successfully")
                .build();
    }
}
