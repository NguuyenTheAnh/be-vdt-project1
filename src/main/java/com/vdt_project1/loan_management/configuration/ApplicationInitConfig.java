package com.vdt_project1.loan_management.configuration;

import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.entity.Role;
import com.vdt_project1.loan_management.enums.AccountStatus;
import com.vdt_project1.loan_management.repository.RoleRepository;
import com.vdt_project1.loan_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Initialize the database with a default admin user
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                Role role = roleRepository.findById("ADMIN")
                        .orElseGet(() -> roleRepository.save(
                                Role.builder()
                                        .name("ADMIN")
                                        .description("Administrator role")
                                        .build()
                        ));

                User user = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .accountStatus(AccountStatus.ACTIVE)
                        .role(role)
                        .build();

                userRepository.save(user);
                log.warn("Admin user created with username: admin and password: admin");
            }
        };
    }
}
