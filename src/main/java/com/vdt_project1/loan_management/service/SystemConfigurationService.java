package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.SystemConfigurationRequest;
import com.vdt_project1.loan_management.dto.response.SystemConfigurationResponse;
import com.vdt_project1.loan_management.entity.SystemConfiguration;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SystemConfigurationService {
    
    SystemConfigurationRepository systemConfigurationRepository;
    
    /**
     * Create a new system configuration
     * @param request the configuration request
     * @return the created configuration response
     */
    @Transactional
    public SystemConfigurationResponse createConfiguration(SystemConfigurationRequest request) {
        log.info("Creating new system configuration with key: {}", request.getConfigKey());
        
        // Check if config key already exists
        if (systemConfigurationRepository.existsByConfigKey(request.getConfigKey())) {
            throw new AppException(ErrorCode.CONFIG_KEY_EXISTS);
        }
        
        SystemConfiguration configuration = SystemConfiguration.builder()
                .configKey(request.getConfigKey())
                .configValue(request.getConfigValue())
                .description(request.getDescription())
                .build();
        
        SystemConfiguration savedConfiguration = systemConfigurationRepository.save(configuration);
        log.info("Successfully created system configuration with ID: {}", savedConfiguration.getConfigId());
        
        return convertToResponse(savedConfiguration);
    }
    
    /**
     * Get all system configurations
     * @return list of all configurations
     */
    public List<SystemConfigurationResponse> getAllConfigurations() {
        log.info("Fetching all system configurations");
        
        List<SystemConfiguration> configurations = systemConfigurationRepository.findAll();
        
        return configurations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get system configuration by ID
     * @param configId the configuration ID
     * @return the configuration response
     */
    public SystemConfigurationResponse getConfigurationById(Long configId) {
        log.info("Fetching system configuration with ID: {}", configId);
        
        SystemConfiguration configuration = systemConfigurationRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.CONFIG_NOT_FOUND));
        
        return convertToResponse(configuration);
    }
    
    /**
     * Get system configuration by key
     * @param configKey the configuration key
     * @return the configuration response
     */
    public SystemConfigurationResponse getConfigurationByKey(String configKey) {
        log.info("Fetching system configuration with key: {}", configKey);
        
        SystemConfiguration configuration = systemConfigurationRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new AppException(ErrorCode.CONFIG_NOT_FOUND));
        
        return convertToResponse(configuration);
    }
    
    /**
     * Update system configuration
     * @param configId the configuration ID
     * @param request the update request
     * @return the updated configuration response
     */
    @Transactional
    public SystemConfigurationResponse updateConfiguration(Long configId, SystemConfigurationRequest request) {
        log.info("Updating system configuration with ID: {}", configId);
        
        SystemConfiguration existingConfiguration = systemConfigurationRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.CONFIG_NOT_FOUND));
        
        // Check if the new config key already exists (excluding current record)
        if (!existingConfiguration.getConfigKey().equals(request.getConfigKey()) && 
            systemConfigurationRepository.existsByConfigKeyAndConfigIdNot(request.getConfigKey(), configId)) {
            throw new AppException(ErrorCode.CONFIG_KEY_EXISTS);
        }
        
        existingConfiguration.setConfigKey(request.getConfigKey());
        existingConfiguration.setConfigValue(request.getConfigValue());
        existingConfiguration.setDescription(request.getDescription());
        
        SystemConfiguration updatedConfiguration = systemConfigurationRepository.save(existingConfiguration);
        log.info("Successfully updated system configuration with ID: {}", configId);
        
        return convertToResponse(updatedConfiguration);
    }
    
    /**
     * Delete system configuration
     * @param configId the configuration ID
     */
    @Transactional
    public void deleteConfiguration(Long configId) {
        log.info("Deleting system configuration with ID: {}", configId);
        
        if (!systemConfigurationRepository.existsById(configId)) {
            throw new AppException(ErrorCode.CONFIG_NOT_FOUND);
        }
        
        systemConfigurationRepository.deleteById(configId);
        log.info("Successfully deleted system configuration with ID: {}", configId);
    }
    
    /**
     * Convert SystemConfiguration entity to response DTO
     * @param configuration the entity
     * @return the response DTO
     */
    private SystemConfigurationResponse convertToResponse(SystemConfiguration configuration) {
        return SystemConfigurationResponse.builder()
                .configId(configuration.getConfigId())
                .configKey(configuration.getConfigKey())
                .configValue(configuration.getConfigValue())
                .description(configuration.getDescription())
                .createdAt(configuration.getCreatedAt())
                .updatedAt(configuration.getUpdatedAt())
                .build();
    }
}
