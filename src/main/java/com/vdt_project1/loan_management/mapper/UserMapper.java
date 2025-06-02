package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.UserCreationRequest;
import com.vdt_project1.loan_management.dto.request.UserUpdateRequest;
import com.vdt_project1.loan_management.dto.response.UserResponse;
import com.vdt_project1.loan_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target = "role", source = "role")
    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);


    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProfile(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
