package com.mjc.school.service;

public interface BaseService<T, R, K> {

    R readById(K id);

    R create(T createRequest);

    R update(K id, T updateRequest);

    boolean deleteById(K id);
}
