package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.VerificationTokenRequest;
import com.vdt_project1.loan_management.dto.response.VerificationTokenResponse;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.entity.VerificationToken;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.VerificationTokenMapper;
import com.vdt_project1.loan_management.repository.UserRepository;
import com.vdt_project1.loan_management.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class VerificationTokenService {
    VerificationTokenRepository verificationTokenRepository;
    UserRepository userRepository;
    VerificationTokenMapper verificationTokenMapper;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }
    public VerificationToken findVerificationTokenByUuid(String uuid) {
        return verificationTokenRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND));
    }

    public VerificationTokenResponse createVerificationToken(VerificationTokenRequest verificationTokenRequest) {
        VerificationToken verificationToken =  verificationTokenMapper.toEntity(verificationTokenRequest);
        User user = findUserById(verificationTokenRequest.getUserId());

        verificationToken.setUser(user);
        verificationToken.setVerified(false);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiresAt(LocalDateTime.now().plusSeconds(60));

        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationTokenMapper.toResponse(verificationToken);
    }

    public VerificationTokenResponse updateVerificationTokenVerified(String uuid){
        VerificationToken verificationToken = findVerificationTokenByUuid(uuid);

        if (verificationToken.getVerified()) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_ALREADY_VERIFIED);
        }

        verificationToken.setVerified(true);
        verificationToken.setExpiresAt(LocalDateTime.now());
        verificationToken = verificationTokenRepository.save(verificationToken);

        return verificationTokenMapper.toResponse(verificationToken);
    }

    public void deleteVerificationToken(String uuid) {
        VerificationToken verificationToken = findVerificationTokenByUuid(uuid);
        verificationTokenRepository.delete(verificationToken);
    }
}
