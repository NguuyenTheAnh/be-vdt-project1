package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.request.VerificationTokenRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.VerificationTokenResponse;
import com.vdt_project1.loan_management.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("verification-tokens")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class VerificationTokenController {
    VerificationTokenService verificationTokenService;

    @PostMapping
    public ApiResponse<VerificationTokenResponse> createVerificationToken(@RequestBody VerificationTokenRequest request) {
        log.info("Creating a new verification token");
        VerificationTokenResponse response = verificationTokenService.createVerificationToken(request);
        return ApiResponse.<VerificationTokenResponse>builder()
                .data(response)
                .build();
    }

    @PatchMapping("/{token}")
    public ApiResponse<VerificationTokenResponse> verifyToken(@PathVariable("token") String token) {
        log.info("Verifying token: {}", token);
        VerificationTokenResponse response = verificationTokenService.updateVerificationTokenVerified(token);
        return ApiResponse.<VerificationTokenResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{token}")
    public ApiResponse<Void> deleteVerificationToken(@PathVariable("token") String token) {
        log.info("Deleting verification token: {}", token);
        verificationTokenService.deleteVerificationToken(token);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
