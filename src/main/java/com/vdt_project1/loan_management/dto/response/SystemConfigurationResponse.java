package com.vdt_project1.loan_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemConfigurationResponse {

    Long configId;
    String configKey;
    String configValue;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
