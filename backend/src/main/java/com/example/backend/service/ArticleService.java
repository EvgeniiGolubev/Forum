package com.example.backend.service;

import com.example.backend.exception.ArticleNotFoundException;
import com.example.backend.exception.FileManagerException;
import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {
    Page<ArticleDto> findAllArticles(String stringSearch, String sortType, int page, int pageSize)
            throws IllegalArgumentException;

    Page<ArticleDto> findArticlesBySubscription(String stringSearch, User user, String sortType, int page, int pageSize)
            throws IllegalArgumentException;

    List<ArticleDto> findAllArticlesByAuthor(User author) throws IllegalArgumentException;

    ArticleDto findArticleById(Long id) throws IllegalArgumentException, ArticleNotFoundException;

    ArticleDto createArticle(User author, ArticleDto articleDto, List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException;

    ArticleDto updateArticle(Long id, User author, ArticleDto articleDto, List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException, ArticleNotFoundException;

    void deleteArticle(Long id, User author)
            throws IllegalArgumentException, FileManagerException, ArticleNotFoundException;

    User getArticleAuthorByArticleId(Long id) throws IllegalArgumentException;
}
