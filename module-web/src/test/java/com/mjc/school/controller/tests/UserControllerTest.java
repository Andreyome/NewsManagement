package com.mjc.school.controller.tests;

import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidRequest_whenSignIn_thenReturnToken() throws Exception {
        UserResponse response = new UserResponse("header.payload.signature");
        when(userService.signIn(any(UserSignIn.class))).thenReturn(response);
        mockMvc.perform(post("/authentication/signIn")
                .contentType("application/json")
                .content("{ \"password\": \"password\", \"username\": \"username\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token",equalTo(response.getToken())))
                .andReturn();
    }
    @Test
    public void givenValidRequest_whenSignUp_thenReturnToken() throws Exception {
        UserResponse response = new UserResponse("header.payload.signature");
        when(userService.signUp(any(UserSignUp.class))).thenReturn(response);
        mockMvc.perform(post("/authentication/signUp")
                        .contentType("application/json")
                        .content("{ \"password\": \"password\", \"username\": \"username\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token",equalTo(response.getToken())))
                .andReturn();
    }
}
