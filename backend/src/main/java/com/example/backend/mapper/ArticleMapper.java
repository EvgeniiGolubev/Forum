package com.example.backend.mapper;

import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {
    public ArticleDto toArticleDto(Article article, User authenticatedUser) throws IllegalArgumentException {
        if (article == null) {
            throw new IllegalArgumentException("Article cannot be null");
        }

        ArticleDto articleDto = new ArticleDto(article);

        Set<User> likes = article.getLikes();
        articleDto.setLikes(likes.size());

        if (authenticatedUser != null) {
            articleDto.setIsLiked(likes.contains(authenticatedUser));
        }

        return articleDto;
    }

    public Page<ArticleDto> toArticleDtoPage(Page<Article> articles, User authenticatedUser) throws IllegalArgumentException {
        if (articles == null) {
            throw new IllegalArgumentException("Articles cannot be null");
        }

        return articles.map(article -> toArticleDto(article, authenticatedUser));
    }

    public List<ArticleDto> toArticleDtoList(List<Article> articles, User authenticatedUser) throws IllegalArgumentException {
        if (articles == null) {
            throw new IllegalArgumentException("Articles cannot be null");
        }

        return articles.stream()
                .map(article -> toArticleDto(article, authenticatedUser))
                .collect(Collectors.toList());
    }
}
