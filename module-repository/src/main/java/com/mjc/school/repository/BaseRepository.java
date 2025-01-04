package com.mjc.school.repository;

import com.mjc.school.repository.model.BaseEntity;
import com.mjc.school.repository.model.PageResponse;

import java.util.Optional;

public interface BaseRepository<T extends BaseEntity<K>, K> {
    PageResponse<T> readAll(Integer page, Integer limit, String sortBy);

    Optional<T> readById(K id);

    T create(T entity);

    T update(K id, T entity);

    boolean deleteById(K id);

    boolean existById(K id);
}
