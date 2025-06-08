package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.UserCreationRequest;
import com.vdt_project1.loan_management.dto.request.UserUpdateUserRequest;
import com.vdt_project1.loan_management.dto.response.RoleDto;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.dto.response.VerificationTokenResponse;
import com.vdt_project1.loan_management.dto.response.VerificationTokenUserDto;
import com.vdt_project1.loan_management.entity.User;
import com.vdt_project1.loan_management.entity.VerificationToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target = "role", source = "user", qualifiedByName = "toRoleDto")
    UserResponse toUserResponse(User user);

    @Named("toRoleDto")
    default RoleDto toRoleDto(User user) {
        if (user == null || user.getRole() == null) {
            return null;
        }

        return RoleDto.builder()
                .name(user.getRole().getName())
                .description(user.getRole().getDescription())
                .build();
    }

    @Mapping(target = "email", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateUserRequest userUpdateUserRequest);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountStatus", ignore = true)
    void updateProfile(@MappingTarget User user, UserUpdateUserRequest userUpdateUserRequest);
}
