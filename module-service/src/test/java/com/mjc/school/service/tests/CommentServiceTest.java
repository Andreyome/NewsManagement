package com.mjc.school.service.tests;

import com.mjc.school.repository.impl.CommentRepositoryImpl;
import com.mjc.school.repository.impl.NewsRepositoryImpl;
import com.mjc.school.repository.model.CommentModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.impl.CommentService;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.validate.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    private final Long commentId = 1L;
    private final String commentContent = "John Doe";
    private LocalDateTime updateDate = LocalDateTime.now();
    private LocalDateTime createDate = LocalDateTime.now();
    private final NewsModel newsModel = new NewsModel("title","content",null,null,null,null,null);

    private final CommentModel commentModel = new CommentModel(1L,commentContent,createDate,updateDate,newsModel);
    private final CommentDtoRequest commentDtoRequest = new CommentDtoRequest(commentContent, 1L);

    @Mock
    private CommentRepositoryImpl commentRepository;
    @Mock
    private NewsRepositoryImpl newsRepository;
    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CommentMapper mapper = Mappers.getMapper(CommentMapper.class);
        Validator validator = new Validator();
        commentService = new CommentService(mapper,commentRepository, validator,newsRepository);
    }


    @Test
    public void testReadByIdComment() {
        when(commentRepository.readById(any())).thenReturn(Optional.of(commentModel));
        CommentDtoResponse response = commentService.readById(commentId);
        assertNotNull(response);
        assertEquals(commentContent,response.content());
    }
    @Test
    public void testReadByIdCommentNotFound() {
        when(commentRepository.readById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> commentService.readById(commentId));
    }
    @Test
    public void testCreateComment() {
        when(newsRepository.readById(any())).thenReturn(Optional.of(newsModel));
        when(commentRepository.create(any())).thenReturn(commentModel);
        CommentDtoResponse response = commentService.create(commentDtoRequest);
        assertNotNull(response);
        assertEquals(commentContent,response.content());
    }
    @Test
    public void testDeleteComment() {
        when(commentRepository.deleteById(any())).thenReturn(true);
        when(commentRepository.existById(any())).thenReturn(true);
        assertTrue(commentService.deleteById(commentId));
    }
    @Test
    public void testDeleteNonExistingComment() {
        when(commentRepository.deleteById(any())).thenReturn(false);
        when(commentRepository.existById(any())).thenReturn(true);
        assertFalse(commentService.deleteById(commentId));
    }

    @Test
    public void testUpdateNonExistingComment() {
        when(commentRepository.existById(any())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.update(commentId, commentDtoRequest));
    }
    @Test
    public void testUpdateCommentSuccess() {
        when(commentRepository.existById(any())).thenReturn(true);
        when(commentRepository.update(any(), any())).thenReturn(commentModel);
        CommentDtoResponse response = commentService.update(commentId, commentDtoRequest);
        assertNotNull(response);
        assertEquals(commentContent,response.content());
    }
    @Test
    public void testCreateCommentWithWrongInput(){
        when(commentRepository.create(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(NotFoundException.class, () -> commentService.create(commentDtoRequest));
    }
    @Test
    public void testReadAllComments() {
        List<CommentModel> commentModelList = List.of(new CommentModel(1L,commentContent,createDate,updateDate,newsModel),new CommentModel(2L,commentContent,createDate,updateDate,newsModel));
        when(commentRepository.readAll(any(), any(),any())).thenReturn(commentModelList);
        List<CommentDtoResponse> responses = commentService.readAll(1,5,"name:desc");
        assertNotNull(responses);
        assertEquals(commentModelList.size(), responses.size());
    }
    @Test
    public void testNonAppropriateLengthContext(){
        when(newsRepository.readById(any())).thenReturn(Optional.of(newsModel));
        when(commentRepository.create(any())).thenReturn(commentModel);
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("low", 1L);
        assertThrows(ValidationException.class, ()->commentService.create(commentDtoRequest));
    }
}
