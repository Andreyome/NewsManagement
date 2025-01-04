package com.mjc.school.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.impl.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTest {
    @MockBean
    private NewsService newsService;
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private final NewsDtoRequest newsExample = new NewsDtoRequest("content", "title","AuthorExampleName",new ArrayList<String>(List.of("tagName")));
    private final NewsDtoResponse mockResponse = new NewsDtoResponse(1L,"content","title",null,null,new AuthorDtoResponse(1L,"AuthorExampleName",null,null),new ArrayList<TagDtoResponse>(List.of(new TagDtoResponse(1L,"TagExample"))),null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(authorities = "User")
    public void testCreate() throws Exception {
        when(newsService.create(newsExample)).thenReturn(mockResponse);
        mockMvc.perform(post("/news")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newsExample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", equalTo("title")))
                .andExpect(jsonPath("$.content", equalTo("content")))
                .andExpect(jsonPath("$.authorDto.id", equalTo(1)))
                .andExpect(jsonPath("$.authorDto.name", equalTo("AuthorExampleName")))
                .andExpect(jsonPath("$.tagDtoResponseList[0].id", equalTo(1)))
                .andExpect(jsonPath("$.tagDtoResponseList[0].name", equalTo("TagExample")));
    }
    @Test
    public void testReadAllNews() throws Exception {
        List<NewsDtoResponse> responseList = List.of(mockResponse,mockResponse);
        when(newsService.readAll(anyInt(),anyInt(),anyString())).thenReturn(new PageDtoResponse<NewsDtoResponse>(responseList,5,5));
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title", equalTo("title")))
                .andExpect(jsonPath("$.items[0].content", equalTo("content")))
                .andExpect(jsonPath("$.items[0].authorDto.id", equalTo(1)))
                .andExpect(jsonPath("$.items[0].authorDto.name", equalTo("AuthorExampleName")))
                .andExpect(jsonPath("$.items[0].tagDtoResponseList[0].id", equalTo(1)))
                .andExpect(jsonPath("$.items[0].tagDtoResponseList[0].name", equalTo("TagExample")))
                .andExpect(jsonPath("$.items[1].tagDtoResponseList[0].id", equalTo(1)))
                .andExpect(jsonPath("$.items[1].tagDtoResponseList[0].name", equalTo("TagExample")))
                .andExpect(jsonPath("$.items[1].authorDto.name", equalTo("AuthorExampleName")))
                .andExpect(jsonPath("$.items[0].content", equalTo("content")));
    }
    @Test
    @WithMockUser(authorities = "User")
    public void testDeleteWithoutProperAuthority_AndReturnForbidden() throws Exception {
        when(newsService.deleteById(anyLong())).thenReturn(true);
        mockMvc.perform(delete("/news/{id}",1))
                .andExpect(status().isForbidden());
    }
    @Test
    public void testDeleteWithoutAuthorisation_AndReturnUnauthorised() throws Exception {
        when(newsService.deleteById(anyLong())).thenReturn(true);
        mockMvc.perform(delete("/news/{id}",1))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(authorities = "Administrator")
    public void testDeleteWithProperAuthority_AndReturnNoContent() throws Exception {
        when(newsService.deleteById(anyLong())).thenReturn(true);
        mockMvc.perform(delete("/news/{id}",1))
                .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser(authorities = "User")
    public void testUpdateWithProperAuthorities_AndReturnIsOk() throws Exception {
        when(newsService.update(anyLong(),any())).thenReturn(mockResponse);
        mockMvc.perform(patch("/news/{id}",1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newsExample)))
                .andExpect(status().isOk());
    }
    @Test
    public void testUpdateWithoutAuthorisation_AndReturnIsUnauthorised() throws Exception {
        when(newsService.update(anyLong(),any())).thenReturn(mockResponse);
        mockMvc.perform(patch("/news/{id}",1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newsExample)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void testReadByParamsWithProperAuthorities_AndReturnIsOk() throws Exception {
        when(newsService.readNewsByParams(any(),any(),anyString(),anyString(),anyString(),any(),any(),any())).thenReturn(new PageDtoResponse<>(List.of(mockResponse),5,1));
        mockMvc.perform(get("/news/byParams")
                        .param("Tag_Ids",String.valueOf(5L), String.valueOf(6L))
                        .param("Tag_Names","TagName","TagName2")
                        .param("Author","AuthorExampleName")
                        .param("Content","content")
                        .param("Title","title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title", equalTo("title")))
                .andExpect(jsonPath("$.items[0].content", equalTo("content")))
                .andExpect(jsonPath("$.items[0].authorDto.id", equalTo(1)));
    }
}
