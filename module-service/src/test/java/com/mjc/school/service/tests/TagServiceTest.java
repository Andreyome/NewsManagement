package com.mjc.school.service.tests;

import com.mjc.school.repository.impl.TagRepositoryImpl;
import com.mjc.school.repository.model.PageResponse;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.impl.TagService;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.validate.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TagServiceTest {

    private final Long TagId = 5L;
    private final String TagName = "Breaking news";

    private final TagModel tagModel = new TagModel(TagId,TagName,null);
    private final TagDtoRequest tagDtoRequest = new TagDtoRequest(TagName);

    @Mock
    private TagRepositoryImpl tagRepositoryImpl;
    @Mock
    private Validator validator;
    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TagMapper tagMapper = Mappers.getMapper(TagMapper.class);
        tagService = new TagService(tagRepositoryImpl,tagMapper,validator);
    }
    @Test
    public void testReadAllTags() {

        List<TagModel> tagModelList = List.of(new TagModel(1L,TagName,null),new TagModel(1L,TagName.toUpperCase(),null));
        when(tagRepositoryImpl.readAll(any(), any(),any())).thenReturn(new PageResponse<>(tagModelList,5,5));
        List<TagDtoResponse> responses = tagService.readAll(1,5,"name:desc");
        assertNotNull(responses);
        assertEquals(tagModelList.size(), responses.size());
    }

    @Test
    public void testReadByIdTag() {
        when(tagRepositoryImpl.readById(any())).thenReturn(Optional.of(tagModel));
        TagDtoResponse response = tagService.readById(TagId);
        assertNotNull(response);
        assertEquals(TagName,response.name());
    }
    @Test
    public void testReadByIdTagNotFound() {
        when(tagRepositoryImpl.readById(any())).thenReturn(Optional.empty());
        Exception exception= assertThrows(NotFoundException.class, () -> tagService.readById(TagId));
        assertEquals(exception.getMessage(),"No tag with such id found");
    }
    @Test
    public void testCreateTag() {
        when(tagRepositoryImpl.create(any())).thenReturn(tagModel);
        TagDtoResponse response = tagService.create(tagDtoRequest);
        assertNotNull(response);
        assertEquals(TagName,response.name());
    }
    @Test
    public void testDeleteTag() {
        when(tagRepositoryImpl.deleteById(any(Long.class))).thenReturn(true);
        assertTrue(tagService.deleteById(TagId));
    }
    @Test
    public void testDeleteNonExistingTag() {
        when(tagRepositoryImpl.deleteById(any(Long.class))).thenReturn(false);
        assertFalse(tagService.deleteById(TagId));
    }

    @Test
    public void testUpdateNonExistingTag() {
        when(tagRepositoryImpl.existById(any(Long.class))).thenReturn(false);
        Exception exception= assertThrows(NotFoundException.class, () -> tagService.update(TagId, tagDtoRequest));
        assertEquals(exception.getMessage(),"No tags with provided id found");
    }
    @Test
    public void testUpdateWithNameTakenTag() {
        when(tagRepositoryImpl.existById(any(Long.class))).thenReturn(true);
        when(tagRepositoryImpl.update(any(),any())).thenThrow(DataIntegrityViolationException.class);
        Exception exception= assertThrows(ValidationException.class, () -> tagService.update(TagId, tagDtoRequest));
        assertEquals(exception.getMessage(),"Tag name is already taken");
    }

    @Test
    public void testUpdateTagSuccess() {
        when(tagRepositoryImpl.existById(any(Long.class))).thenReturn(true);
        when(tagRepositoryImpl.update(any(), any())).thenReturn(tagModel);
        TagDtoResponse response = tagService.update(TagId, tagDtoRequest);
        assertNotNull(response);
        assertEquals(TagName,response.name());
    }
    @Test
    public void testCreateTagWithWrongInput(){
        when(tagRepositoryImpl.create(any())).thenThrow(new DataIntegrityViolationException(""));
        Exception exception = assertThrows(ValidationException.class, () -> tagService.create(tagDtoRequest));
        assertEquals(exception.getMessage(),"Tag name is already taken");
    }
}
