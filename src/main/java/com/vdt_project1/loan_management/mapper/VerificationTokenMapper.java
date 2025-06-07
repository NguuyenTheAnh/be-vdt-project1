package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.VerificationTokenRequest;
import com.vdt_project1.loan_management.dto.response.VerificationTokenResponse;
import com.vdt_project1.loan_management.dto.response.VerificationTokenUserDto;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.entity.VerificationToken;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface VerificationTokenMapper {

    VerificationToken toEntity(VerificationTokenRequest verificationTokenRequest);

    void updateEntityFromRequest(VerificationTokenRequest verificationTokenRequest, @MappingTarget VerificationToken verificationToken);

    @Mapping(target = "user", source = "verificationToken", qualifiedByName = "toUserDto")
    VerificationTokenResponse toResponse(VerificationToken verificationToken);

    @Named("toUserDto")
    default VerificationTokenUserDto toUserDto( VerificationToken verificationToken) {
        if (verificationToken == null || verificationToken.getUser() == null) {
            return null;
        }

        User user = verificationToken.getUser();

        return VerificationTokenUserDto.builder()
                .userId(user.getId())
                .userEmail(user.getEmail())
                .build();
    }
}
