package com.mjc.school.service;

import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;

import java.util.List;

public interface AuthorServInterface extends BaseService<AuthorDtoRequest, AuthorDtoResponse,Long> {
    List<AuthorDtoResponse> readAll(Integer page, Integer limit, String sortBy);
    AuthorDtoResponse getAuthorByNewsId(Long id);
}
