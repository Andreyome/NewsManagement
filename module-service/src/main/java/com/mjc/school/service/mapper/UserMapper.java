package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(com.mjc.school.repository.model.Roles.User)")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    UserModel userDtoToModel(UserSignUp userSignUp);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    UserModel userDtoToModel(UserSignIn userSignin);
}
