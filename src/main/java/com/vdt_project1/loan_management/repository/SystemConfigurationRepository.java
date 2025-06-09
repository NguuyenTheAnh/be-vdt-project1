package com.vdt_project1.loan_management.repository;

import com.vdt_project1.loan_management.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    
    /**
     * Find system configuration by config key
     * @param configKey the configuration key
     * @return Optional containing the configuration if found
     */
    Optional<SystemConfiguration> findByConfigKey(String configKey);
    
    /**
     * Check if a configuration key already exists
     * @param configKey the configuration key to check
     * @return true if exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);
    
    /**
     * Check if a configuration key exists for update (excluding current id)
     * @param configKey the configuration key to check
     * @param configId the current config ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(sc) > 0 FROM SystemConfiguration sc WHERE sc.configKey = :configKey AND sc.configId != :configId")
    boolean existsByConfigKeyAndConfigIdNot(@Param("configKey") String configKey, @Param("configId") Long configId);
}
