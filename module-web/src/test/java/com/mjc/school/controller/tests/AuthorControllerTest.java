package com.mjc.school.controller.tests;

import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.impl.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTest {
    @MockBean
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(authorities = "User")
    public void givenValidRequest_whenCreateAuthor_thenReturn201() throws Exception {
        AuthorDtoResponse mockResponse = new AuthorDtoResponse(1L, "Robertson", null, null);
        when(authorService.create(any(AuthorDtoRequest.class))).thenReturn(mockResponse);

        AuthorDtoRequest authorDtoRequest = new AuthorDtoRequest("Robertson");
        MvcResult result = mockMvc.perform(post("/author").contentType("application/json").content("{\"name\" : \"Robertson\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("Robertson")))
                .andReturn();
    }

    @Test
    public void readAllAuthors_whenValidRequest_thenReturn200() throws Exception {
        List<AuthorDtoResponse> mockResponse = Arrays.asList(new AuthorDtoResponse(1L, "Robertson", null, null), new AuthorDtoResponse(2L, "Rebecca", null, null));
        when(authorService.readAll(any(Integer.class), any(Integer.class), any(String.class))).thenReturn(mockResponse);
        mockMvc.perform(get("/author").param("limit", "5").param("page", "1").param("sortBy", "name:desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Robertson")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].name", equalTo("Rebecca")))
                .andReturn();
    }


    @Test
    public void testReadByIdMethod() throws Exception {
        AuthorDtoResponse mockResponse = new AuthorDtoResponse(1L, "Robertson", null, null);
        when(authorService.readById(any())).thenReturn(mockResponse);
        mockMvc.perform(get("/author/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Robertson")))
                .andReturn();
    }

    @Test
    public void testDeleteAuthorWithUnmatchingUsername_whenValidRequest_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/author/{id}", 1L))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    public void testDeleteAuthorWithMatchingUsername_whenValidRequest_thenReturnOk() throws Exception {
        when(authorService.deleteById(any())).thenReturn(true);
        mockMvc.perform(delete("/author/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "User")
    public void testDeleteAuthorWithUserAuthority_whenValidRequest_thenReturnForbidden() throws Exception {
        when(authorService.deleteById(any())).thenReturn(true);
        mockMvc.perform(delete("/author/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}
