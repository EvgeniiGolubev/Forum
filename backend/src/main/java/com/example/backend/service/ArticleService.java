package com.example.backend.service;

import com.example.backend.exception.ArticleNotFoundException;
import com.example.backend.exception.FileManagerException;
import com.example.backend.exception.UserAuthenticationException;
import com.example.backend.model.dto.ArticleDto;
import com.example.backend.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    Page<ArticleDto> findAllArticles(String sortType, int page, int pageSize) throws IllegalArgumentException;
    ArticleDto findArticleById(Long id) throws IllegalArgumentException, ArticleNotFoundException;

    ArticleDto createArticle(
            UserDetailsImpl authenticatedUser,
            ArticleDto articleDto,
            List<MultipartFile> images
    ) throws
            IllegalArgumentException,
            UserAuthenticationException,
            FileManagerException;

    ArticleDto updateArticle(
            Long id,
            UserDetailsImpl authenticatedUser,
            ArticleDto articleDto,
            List<MultipartFile> images
    ) throws
            AccessDeniedException,
            IllegalArgumentException,
            UserAuthenticationException,
            FileManagerException,
            ArticleNotFoundException;

    void deleteArticle(
            Long id,
            UserDetailsImpl authenticatedUser
    ) throws
            AccessDeniedException,
            IllegalArgumentException,
            UserAuthenticationException,
            FileManagerException,
            ArticleNotFoundException;
}
