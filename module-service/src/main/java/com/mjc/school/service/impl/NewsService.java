package com.mjc.school.service.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.impl.AuthorRepositoryImpl;
import com.mjc.school.repository.impl.TagRepositoryImpl;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.PageResponse;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.NewsServInterface;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.validate.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsService implements NewsServInterface {
    private final NewsRepository newsRepository;
    private final NewsMapper mapper;
    private final TagRepositoryImpl tagRepository;
    private final AuthorRepositoryImpl authorRepository;
    private final Validator validator;


    @Autowired
    public NewsService(NewsRepository newsRepository, NewsMapper mapper, TagRepositoryImpl tagRepository, AuthorRepositoryImpl authorRepository, Validator validator) {
        this.newsRepository = newsRepository;
        this.mapper = mapper;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
        this.validator = validator;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<NewsDtoResponse> readAll(Integer page, Integer limit, String sortBy) {
        try {
            PageResponse<NewsModel> responseFromRepository = newsRepository.readAll(page, limit, sortBy);
            long totalItems = responseFromRepository.getTotalElements();
            int totalPages = responseFromRepository.getTotalPages();
            List<NewsDtoResponse> newsDtoResponses =responseFromRepository.getItems().stream().map(mapper::newsToDto).collect(Collectors.toList());
            return new PageDtoResponse<>(newsDtoResponses, totalItems,totalPages );
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidationException("Wrong parameters for method provided.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDtoResponse readById(Long id) {
        Optional<NewsModel> newsModelOptional = newsRepository.readById(id);
        if (newsModelOptional.isPresent()) {
            return mapper.newsToDto(newsModelOptional.get());
        } else throw new NotFoundException("No news with such id found");
    }

    @Override
    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public NewsDtoResponse create(NewsDtoRequest createRequest) {
        try {
            validator.validateNews(createRequest);
            createNonExistingAuthor(createRequest.authorName());
            createNonExistingTags(createRequest.tagNames());
            NewsModel newsModel = mapper.newsDtoToModel(createRequest);
            newsModel.setCreateDate(LocalDateTime.now());
            newsModel.setLastUpdateDate(LocalDateTime.now());
            newsRepository.create(newsModel);
            return mapper.newsToDto(newsModel);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Provided news info has already been used.");
        }
    }

    @Override
    @Transactional
    public NewsDtoResponse update(Long id, NewsDtoRequest updateRequest) {
        if (!newsRepository.existById(id)) {
            throw new NotFoundException("No news with provided id found");
        }
        try {
            validator.validateNews(updateRequest);
            createNonExistingAuthor(updateRequest.authorName());
            createNonExistingTags(updateRequest.tagNames());
            NewsModel updatedNews = mapper.newsDtoToModel(updateRequest);
            updatedNews.setLastUpdateDate(LocalDateTime.now());
            updatedNews.setCreateDate(newsRepository.readById(id).get().getCreateDate());
            return mapper.newsToDto(newsRepository.update(id, updatedNews));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Provided news info has already been used.");
        }
    }


    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return newsRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<NewsDtoResponse> readNewsByParams(List<Long> tagsIds, List<String> tagsNames, String authorName, String title, String content,Integer page, Integer pageSize, String sortBy) {
        PageResponse<NewsModel> newsModelPageResponse = newsRepository.readNewsByParams(tagsIds, tagsNames, authorName, title, content,page,pageSize,sortBy);
        return new PageDtoResponse<NewsDtoResponse>(mapper.modelListToDtoList(newsModelPageResponse.getItems()),newsModelPageResponse.getTotalElements(),newsModelPageResponse.getTotalPages());
    }

     public void createNonExistingTags(List<String> tagNames) {
        tagNames.stream().filter(tagName -> tagRepository.readTagByName(tagName).isEmpty())
                .forEach(nonExistTagName -> {
                    TagModel tag = new TagModel();
                    tag.setName(nonExistTagName);
                    tagRepository.create(tag);
                });
    }

    public void createNonExistingAuthor(String name) {
        if (name != null && !name.equals("")) {
            if (authorRepository.readByName(name).isEmpty()) {
                AuthorModel authorModel = new AuthorModel();
                authorModel.setName(name);
                authorRepository.create(authorModel);
            }
        }
    }
}
