package com.mjc.school.service.tests;

import com.mjc.school.repository.impl.UserRepository;
import com.mjc.school.repository.model.Roles;
import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.configuration.SecurityConfiguration;
import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.impl.JwtService;
import com.mjc.school.service.impl.UserService;
import com.mjc.school.service.mapper.UserMapper;
import org.jboss.jandex.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
public class UserServiceTest {
    private final Long userId = 5L;
    private final String username = "johnWitch";
    private final String password = "continental";
    private final UserModel userModel = new UserModel(userId, username, password, Roles.User);
    private final UserSignIn signInRequest = new UserSignIn(username, password);
    private final UserSignUp signUpRequest = new UserSignUp(username, password);
    @InjectMocks
    private UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    private final JwtService jwtService = new JwtService();
    @Mock
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, jwtService, authenticationManager, passwordEncoder,userMapper);
    }
    @Test
    void testSignUp() {
        when(userMapper.userDtoToModel(any(UserSignUp.class))).thenReturn(userModel);
        when(userRepository.save(any())).thenReturn(userModel);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        UserResponse response = userService.signUp(signUpRequest);
        assertNotNull(response);
        assertEquals(response.getToken(),jwtService.generateToken(userModel));
    }
    @Test
    void testSignIn() {
        when(userMapper.userDtoToModel(any(UserSignIn.class))).thenReturn(userModel);
        when(userRepository.save(any())).thenReturn(userModel);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userModel));
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(username, password));
        UserResponse response = userService.signIn(signInRequest);
        assertNotNull(response);
        assertEquals(response.getToken(),jwtService.generateToken(userModel));
    }




}
