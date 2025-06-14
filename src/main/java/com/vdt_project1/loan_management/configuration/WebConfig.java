package com.vdt_project1.loan_management.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String directory = uploadProperties.getDirectory();
        // Đảm bảo đường dẫn kết thúc bằng /
        if (!directory.endsWith("/")) {
            directory += "/";
        }

        registry
                .addResourceHandler("/uploads/**") // URL public
                .addResourceLocations("file:" + directory); // Đường dẫn thư mục động
    }

}
