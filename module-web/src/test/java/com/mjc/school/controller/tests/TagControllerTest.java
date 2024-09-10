package com.mjc.school.controller.tests;

import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.impl.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerTest {
    @MockBean
    private TagService tagService;
    @Autowired
    private MockMvc mockMvc;
    private final TagDtoResponse mockResponse =new TagDtoResponse(1L, "Breaking news");
    private final String tagExample = "{\"name\":\"Breaking news\"}";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @WithMockUser(authorities = "User")
    public void testCreateTag() throws Exception {
        when(tagService.create(any(TagDtoRequest.class))).thenReturn(mockResponse);
        mockMvc.perform(post("/tag").contentType("application/json").content(tagExample))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("Breaking news")))
                .andReturn();
    }
    @Test
    public void testReadTag() throws Exception {
        when(tagService.readById(any(Long.class))).thenReturn(mockResponse);
        mockMvc.perform(get("/tag/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Breaking news")))
                .andReturn();
    }
    @Test
    public void testReadAllTag() throws Exception {
        List<TagDtoResponse> mockResponse = Arrays.asList(
                new TagDtoResponse(1L, "Breaking news"),
                new TagDtoResponse(2L, "Weather"));

        when(tagService.readAll(any(Integer.class),any(Integer.class),any(String.class))).thenReturn(mockResponse);
        mockMvc.perform(get("/tag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", equalTo("Breaking news")))
                .andExpect(jsonPath("$[1].name", equalTo("Weather")))
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    public void testDeleteTag() throws Exception {
        when(tagService.deleteById(any(Long.class))).thenReturn(true);
        mockMvc.perform(delete("/tag/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
