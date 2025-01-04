package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.PageResponse;
import com.mjc.school.repository.model.TagModel;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Repository("NewsRepository")
public class NewsRepositoryImpl extends AbstractDBRepository<NewsModel, Long> implements NewsRepository {
    public PageResponse<NewsModel> readNewsByParams(List<Long> tagsIds, List<String> tagsNames, String authorName, String title, String content, Integer page, Integer pageSize, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Main query for fetching News
        CriteriaQuery<NewsModel> criteriaQuery = criteriaBuilder.createQuery(NewsModel.class);
        Root<NewsModel> root = criteriaQuery.from(NewsModel.class);
        criteriaQuery.select(root);

        Predicate predicate = criteriaBuilder.conjunction();

        // Join for tags
        Join<NewsModel, TagModel> tagJoin = root.join("tagModelList");

        // Handle tag IDs filtering
        if (tagsIds != null && !tagsIds.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, tagJoin.get("id").in(tagsIds));
        }

        // Handle tag names filtering
        if (tagsNames != null && !tagsNames.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, tagJoin.get("name").in(tagsNames));
        }

        // Handle author name filtering
        if (authorName != null && !authorName.isBlank()) {
            Join<AuthorModel, NewsModel> na = root.join("authorModel");
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(na.get("name"), "%" + authorName + "%"));
        }

        // Handle title and content filtering
        if ((title != null && !title.isBlank()) || (content != null && !content.isBlank())) {
            Predicate titlePredicate = null;
            Predicate contentPredicate = null;

            if (title != null && !title.isBlank()) {
                titlePredicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            }

            if (content != null && !content.isBlank()) {
                contentPredicate = criteriaBuilder.like(root.get("content"), "%" + content + "%");
            }

            if (titlePredicate != null && contentPredicate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(titlePredicate, contentPredicate));
            } else if (titlePredicate != null) {
                predicate = criteriaBuilder.and(predicate, titlePredicate);
            } else if (contentPredicate != null) {
                predicate = criteriaBuilder.and(predicate, contentPredicate);
            }
        }

        criteriaQuery.where(predicate);
        criteriaQuery.groupBy(
                root.get("id"),
                root.get("title"),
                root.get("content"),
                root.get("createDate"),
                root.get("lastUpdateDate"),
                root.get("authorModel").get("name"), // Include authorModel.name in groupBy
                root.get("authorModel"),
                root.get("authorModel").get("id")// Add authorModel itself if necessary
        );

        // Grouping to filter by count of tags
        criteriaQuery.groupBy(root.get("id"));
        if (tagsNames != null && !tagsNames.isEmpty()) {
            // Correctly convert tagsNames.size() to Long
            criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.count(tagJoin), Long.valueOf(tagsNames.size())));
        }

// Sorting logic
        if (sortBy != null && !sortBy.isBlank()) {
            String[] sortBySpecification = sortBy.split(":");
            System.out.println(sortBySpecification[0]);
            System.out.println(sortBySpecification[1]);
            if (sortBySpecification.length == 2) {
                String sortField = sortBySpecification[0];
                String sortDirection = sortBySpecification[1].equalsIgnoreCase("ASC") ? "ASC" : "DESC";

                if (sortField.equalsIgnoreCase("author")) {
                    Join<NewsModel, AuthorModel> authorJoin = root.join("authorModel");
                    if (sortDirection.equalsIgnoreCase("ASC")) {
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("authorModel")));
                    } else {
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("authorModel")));
                    }
                } else {
                    // Default sorting by other fields
                    if (sortDirection.equalsIgnoreCase("ASC")) {
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sortField)));
                    } else {
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sortField)));
                    }
                }
            }
        }

        // Pagination logic
        if (page == null || page < 1) {
            page = 1; // Default to page 1
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10; // Default page size
        }
        int startIndex = (page - 1) * pageSize;

        TypedQuery<NewsModel> typedQuery = entityManager.createQuery(criteriaQuery);
        List<NewsModel> resultList = typedQuery.getResultList();
        Long totalElements= (long)resultList.size();

        typedQuery.setFirstResult(startIndex); // Set the starting index
        typedQuery.setMaxResults(pageSize); // Set the max number of results to return
        
        // Log the total elements to debug
        System.out.println("Total Elements: " + totalElements);

        return new PageResponse<>(typedQuery.getResultList(), totalElements, (int) Math.ceil((double) totalElements / pageSize));
    }

    @Override
    void updateExistingModel(NewsModel existingEntity, NewsModel updatedEntity) {
        existingEntity.setContent(updatedEntity.getContent());
        existingEntity.setTitle(updatedEntity.getTitle());
        existingEntity.setAuthorModel(updatedEntity.getAuthorModel());
        existingEntity.setTagModelList(updatedEntity.getTagModelList());
    }

}
