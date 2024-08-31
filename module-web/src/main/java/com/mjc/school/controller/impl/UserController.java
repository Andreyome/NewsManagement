package com.mjc.school.controller.impl;

import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.impl.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
@RequestMapping(value = "/authentication",produces = {"application/JSON"})
@RequiredArgsConstructor
@Api(produces = "application/JSON", value = "Authorization")
public class UserController {
    private final UserService userService;
    @PostMapping("/signUp")
    @ApiOperation(value = "Sign up", response = TagDtoResponse.class)
    public UserResponse signUp(@RequestBody UserSignUp request) {
        return userService.signUp(request);
    }
    @PostMapping("/signIn")
    @ApiOperation(value = "Sign In ", response = TagDtoResponse.class)
    public UserResponse signIn(@RequestBody UserSignIn request) {
        return userService.signIn(request);
    }
}
