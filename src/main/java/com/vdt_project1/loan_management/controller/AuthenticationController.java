package com.vdt_project1.loan_management.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.vdt_project1.loan_management.dto.request.*;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.AuthenticationResponse;
import com.vdt_project1.loan_management.dto.response.IntrospectResponse;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.service.AuthenticationService;
import com.vdt_project1.loan_management.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest)
            throws KeyLengthException {
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("logout")
    ApiResponse<Void> logout(@RequestBody InvalidatedTokenRequest request) throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("introspect")
    ApiResponse<IntrospectResponse> login(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @GetMapping("/my-profile")
    ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyProfile())
                .build();
    }

        @PostMapping("/password-reset/email/{email}")
    ApiResponse<String> sendVerificationEmailPasswordReset(@PathVariable String email )  throws MessagingException {
        return ApiResponse.<String>builder()
                .data(authenticationService.sendVerificationEmailPasswordReset(email))
                .build();
    }

    @PostMapping("/password-reset/token/{token}")
    ApiResponse<Boolean> isValidPasswordResetToken(@PathVariable String token ) {
        return ApiResponse.<Boolean>builder()
                .data(authenticationService.isValidPasswordResetToken(token ))
                .build();
    }

    @PostMapping("/password-reset")
    ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }
}
