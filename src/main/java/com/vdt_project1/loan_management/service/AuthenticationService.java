package com.vdt_project1.loan_management.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vdt_project1.loan_management.dto.request.*;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.AuthenticationResponse;
import com.vdt_project1.loan_management.dto.response.IntrospectResponse;
import com.vdt_project1.loan_management.entity.InvalidatedToken;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.entity.VerificationToken;
import com.vdt_project1.loan_management.enums.AccountStatus;
import com.vdt_project1.loan_management.enums.VerificationTokenType;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.repository.InvalidatedTokenRepository;
import com.vdt_project1.loan_management.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    EmailService emailService;
    VerificationTokenService verificationTokenService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.access-token-expiration}")
    protected long ACCESS_TOKEN_EXPIRATION;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    protected long REFRESH_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws KeyLengthException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        boolean isAuthenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!isAuthenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // check status of user account
        if(user.getAccountStatus() == null || user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_ACCOUNT_INACTIVE);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expirationTime(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        String newToken = generateToken(user);
        return AuthenticationResponse.builder()
                .token(newToken)
                .isAuthenticated(true)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {

        boolean isValid = true;
        try {
            verifyToken(introspectRequest.getToken(), false);
        } catch (Exception e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        log.info("Verifying token: {}", token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);
        var verified = signedJWT.verify(verifier);
        Date expirationTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plusSeconds(REFRESH_DURATION).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!verified || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) throws KeyLengthException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity_service")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRATION).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.err.println("Error generating JWT: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRole() != null) {
            stringJoiner.add("ROLE_" + user.getRole().getName());
            if (!CollectionUtils.isEmpty(user.getRole().getPermissions())) {
                user.getRole().getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            }
        }
        return stringJoiner.toString();
    }

    public void logout(InvalidatedTokenRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jti = signToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jti).expirationTime(expirationTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    public String sendVerificationEmailPasswordReset(String email) throws MessagingException {
        log.info("Sending verification email for password reset to: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        String verificationToken = UUID.randomUUID().toString();
        emailService.sendHtmlTemplateEmail(
                user.getEmail(),
                "Xác thực tài khoản - LoanConv",
                user.getFullName(),
                verificationToken
        );
        VerificationTokenRequest verificationTokenRequest = VerificationTokenRequest.builder()
                .userId(user.getId())
                .uuid(verificationToken)
                .type(VerificationTokenType.PASSWORD_RESET)
                .build();

        verificationTokenService.createVerificationToken(verificationTokenRequest);

        return null;
    }

    public String sendVerificationEmailAccountActivation(String email) throws MessagingException {
        log.info("Sending verification email to: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        String verificationToken = UUID.randomUUID().toString();
        emailService.sendHtmlTemplateEmail(
                user.getEmail(),
                "Xác thực tài khoản - LoanConv",
                user.getFullName(),
                verificationToken
        );
        VerificationTokenRequest verificationTokenRequest = VerificationTokenRequest.builder()
                .userId(user.getId())
                .uuid(verificationToken)
                .type(VerificationTokenType.ACCOUNT_ACTIVATION)
                .build();

        verificationTokenService.createVerificationToken(verificationTokenRequest);

        return null;
    }

    public Boolean isValidPasswordResetToken(String uuid) {
        log.info("Checking if password reset token is verified for UUID: {}", uuid);
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByUuid(uuid);

        if (verificationToken.getType() != VerificationTokenType.PASSWORD_RESET) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN_TYPE);
        }

        if(verificationToken.getExpiresAt() == null || verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }

        if (!verificationToken.getVerified()) {
            return true;
        } else {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_ALREADY_VERIFIED);
        }
    }

    public Boolean isValidAccountActivationToken(String uuid) {
        log.info("Checking if account activation token is verified for UUID: {}", uuid);
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByUuid(uuid);

        if (verificationToken.getType() != VerificationTokenType.ACCOUNT_ACTIVATION) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN_TYPE);
        }

        if(verificationToken.getExpiresAt() == null || verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }

        if (!verificationToken.getVerified()) {
            return true;
        } else {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_ALREADY_VERIFIED);
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for user with email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        verificationTokenService.updateVerificationTokenVerified(request.getToken());
    }

    public void activateAccount(ActivateAccountRequest request) {
        log.info("Activating account for user with email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        verificationTokenService.updateVerificationTokenVerified(request.getToken());
    }

}
