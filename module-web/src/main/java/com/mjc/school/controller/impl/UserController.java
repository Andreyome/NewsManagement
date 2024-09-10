package com.mjc.school.controller.impl;

import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.impl.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Sign up", response = UserResponse.class)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created user"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Internal resource not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public UserResponse signUp(@RequestBody UserSignUp request) {
        return userService.signUp(request);
    }
    @PostMapping("/signIn")
    @ApiOperation(value = "Sign In ", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully signed in"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Internal resource not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public UserResponse signIn(@RequestBody UserSignIn request) {
        return userService.signIn(request);
    }
}
