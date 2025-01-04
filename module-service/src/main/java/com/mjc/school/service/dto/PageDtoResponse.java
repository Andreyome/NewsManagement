package com.mjc.school.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Setter
@Getter
public class PageDtoResponse<T> {
        private List<T> items;
        private long totalElements;
        private int totalPages;
}
