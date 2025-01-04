package com.mjc.school.service.tests;
import com.mjc.school.repository.impl.AuthorRepositoryImpl;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.PageResponse;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.impl.AuthorService;
import com.mjc.school.service.validate.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthorServiceTest {
    private final Long authorId = 5L;
    private final String authorName = "John Doe";
    private LocalDateTime updateDate = LocalDateTime.now();
    private LocalDateTime createDate = LocalDateTime.now();

    private final AuthorModel authorModel = new AuthorModel( authorName,createDate,updateDate);
    private final AuthorDtoRequest authorDtoRequest = new AuthorDtoRequest(authorName);

    @Mock
    private AuthorRepositoryImpl authorRepositoryImpl;
    @Mock
    private Validator validator;
    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorService = new AuthorService(authorRepositoryImpl, validator);
    }
    @Test
    public void testReadAllAuthors() {
        List<AuthorModel> authorModelList = List.of(new AuthorModel(authorName,createDate,updateDate),new AuthorModel(authorName.toUpperCase(),createDate,updateDate));
        when(authorRepositoryImpl.readAll(any(), any(),any())).thenReturn(new PageResponse<AuthorModel>(authorModelList,5,5));
        List<AuthorDtoResponse> responses = authorService.readAll(1,5,"name:desc");
        assertNotNull(responses);
        assertEquals(authorModelList.size(), responses.size());
    }

    @Test
    public void testReadByIdAuthor() {
        when(authorRepositoryImpl.readById(any())).thenReturn(Optional.of(authorModel));
        AuthorDtoResponse response = authorService.readById(authorId);
        assertNotNull(response);
        assertEquals(authorName,response.name());
    }
    @Test
    public void testReadByIdAuthorNotFound() {
        when(authorRepositoryImpl.readById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> authorService.readById(authorId));
    }
    @Test
    public void testCreateAuthor() {
        when(authorRepositoryImpl.create(any())).thenReturn(authorModel);
        AuthorDtoResponse response = authorService.create(authorDtoRequest);
        assertNotNull(response);
        assertEquals(authorName,response.name());
    }
    @Test
    public void testDeleteAuthor() {
        when(authorRepositoryImpl.deleteById(any(Long.class))).thenReturn(true);
        assertTrue(authorService.deleteById(authorId));
    }
    @Test
    public void testDeleteNonExistingAuthor() {
        when(authorRepositoryImpl.deleteById(any(Long.class))).thenReturn(false);
        assertFalse(authorService.deleteById(authorId));
    }

    @Test
    public void testUpdateNonExistingAuthor() {
        when(authorRepositoryImpl.existById(any(Long.class))).thenReturn(false);
        assertThrows(NotFoundException.class, () -> authorService.update(authorId,authorDtoRequest));
    }
    @Test
    public void testUpdateAuthorSuccess() {
    when(authorRepositoryImpl.existById(any(Long.class))).thenReturn(true);
    when(authorRepositoryImpl.update(any(), any())).thenReturn(authorModel);
    AuthorDtoResponse response = authorService.update(authorId,authorDtoRequest);
    assertNotNull(response);
    assertEquals(authorName,response.name());
    }
    @Test
    public void testCreateAuthorWithWrongInput(){
        when(authorRepositoryImpl.create(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(ValidationException.class, () -> authorService.create(authorDtoRequest));
    }
}
