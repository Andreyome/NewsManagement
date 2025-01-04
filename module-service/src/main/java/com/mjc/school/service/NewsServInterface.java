package com.mjc.school.service;


import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;

import java.util.List;

public interface NewsServInterface extends BaseService<NewsDtoRequest,NewsDtoResponse,Long> {

    PageDtoResponse<NewsDtoResponse> readNewsByParams(List<Long> tagsIds, List<String> tagsNames, String authorName, String title, String content,Integer page, Integer pageSize, String sortBy);
    PageDtoResponse<NewsDtoResponse> readAll(Integer page, Integer limit, String sortBy);
}
