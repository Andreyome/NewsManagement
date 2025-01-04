package com.mjc.school.repository.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Setter
@Getter
public class PageResponse<T> {
    private List<T> items;
    private long totalElements;
    private int totalPages;
}