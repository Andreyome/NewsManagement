package com.mjc.school.repository;

import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.PageResponse;

import java.util.List;

public interface NewsRepository extends BaseRepository<NewsModel,Long> {
    PageResponse<NewsModel> readNewsByParams(List<Long> tagsIds, List<String> tagsNames, String authorName, String title, String content,Integer page, Integer pageSize, String sortBy);
}
