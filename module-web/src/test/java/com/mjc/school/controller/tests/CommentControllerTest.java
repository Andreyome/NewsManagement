package com.mjc.school.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.impl.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
    @MockBean
    private CommentService commentService;
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private final CommentDtoRequest commentExample = new CommentDtoRequest("content", 1L);
    private final CommentDtoResponse mockResponse = new CommentDtoResponse(1L,"content",null,null,1L);
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }
    @Test
    @WithMockUser(authorities = "User")
    public void testCreateComment() throws Exception {
        when(commentService.create(any(CommentDtoRequest.class))).thenReturn(mockResponse);
        mockMvc.perform(post("/comment").contentType("application/json").content(objectMapper.writeValueAsString(commentExample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", equalTo("content")))
                .andReturn();
    }
    @Test
    @WithMockUser(authorities = "Administrator")
    public void testDeleteComment() throws Exception {
        when(commentService.deleteById(any(Long.class))).thenReturn(true);
        mockMvc.perform(delete("/comment/{id}",1))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testDeleteCommentWhenUnauthorised() throws Exception {
        when(commentService.deleteById(any(Long.class))).thenReturn(true);
        mockMvc.perform(delete("/comment/{id}",1))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(authorities = "User")
    public void testDeleteCommentWhenNotAdministrator() throws Exception {
        when(commentService.deleteById(any(Long.class))).thenReturn(true);
        mockMvc.perform(delete("/comment/{id}",1))
                .andExpect(status().isForbidden());
    }
    @Test
    public void testReadByIdComment() throws Exception {
        when(commentService.readById(any(Long.class))).thenReturn(mockResponse);
        mockMvc.perform(get("/comment/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", equalTo("content")))
                .andReturn();
    }


}
