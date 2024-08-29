package com.mjc.school.controller.UserController;

import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.userService.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/signUp")
    public UserResponse signUp(@RequestBody UserSignUp request) {
        return userService.signUp(request);
    }
    @PostMapping("/signIn")
    public UserResponse signIn(@RequestBody UserSignIn request) {
        return userService.signIn(request);
    }
}
