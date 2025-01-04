package com.mjc.school.controller;

import com.mjc.school.service.dto.PageDtoResponse;
import org.springframework.hateoas.EntityModel;

public interface NewsControllerInterface<T, R, K> {

    PageDtoResponse<R> readAll(Integer page, Integer limit, String sortBy);

    EntityModel<R> readById(K id);

    EntityModel<R> create(T createRequest);

    EntityModel<R> update(K Id, T updateRequest);

    void deleteById(K id);
}