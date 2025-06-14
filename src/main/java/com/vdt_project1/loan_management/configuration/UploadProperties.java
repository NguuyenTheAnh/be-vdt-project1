package com.vdt_project1.loan_management.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    private String directory = "uploads";
    private String baseUrl = "http://localhost:8080/api/uploads";
    private long maxSize = 52428800; // 50MB
    private String allowedExtensions = "pdf,jpg,jpeg,png,doc,docx";
}
