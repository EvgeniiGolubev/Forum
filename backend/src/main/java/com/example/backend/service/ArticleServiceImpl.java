package com.example.backend.service;

import com.example.backend.exception.ArticleNotFoundException;
import com.example.backend.exception.FileManagerException;
import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.ArticleRepository;
import com.example.backend.utils.FileManagerUtil;
import com.example.backend.utils.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final FileManagerUtil fileManagerUtil;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, FileManagerUtil fileManagerUtil) {
        this.articleRepository = articleRepository;
        this.fileManagerUtil = fileManagerUtil;
    }

    @Override
    public Page<ArticleDto> findAllArticles(String sortType, int page, int pageSize) throws IllegalArgumentException {
        Pageable pageable = PageableUtil.validPaginationAndGetPageable(sortType, page, pageSize, "creationDate");
        Page<Article> articles = articleRepository.findAll(pageable);

        return articles.map(ArticleDto::new);
    }

    @Override
    public ArticleDto findArticleById(Long id) throws IllegalArgumentException, ArticleNotFoundException {
        Article article = checkArticlePresentAndGet(id);

        return new ArticleDto(article);
    }

    @Override
    public ArticleDto createArticle(User author, ArticleDto articleDto, List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException {

        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (articleDto == null) {
            throw new IllegalArgumentException("New article cannot be null");
        }

        List<String> imageLinks = fileManagerUtil.saveFilesAndGetLinks(images);

        Article newArticle = new Article(
                articleDto.getTitle(),
                articleDto.getContent(),
                author,
                LocalDateTime.now()
        );

        if (imageLinks != null && !imageLinks.isEmpty()) {
            newArticle.getImageLinks().addAll(imageLinks);
        }

        return new ArticleDto(articleRepository.save(newArticle));
    }

    @Override
    public ArticleDto updateArticle(Long id, User author, ArticleDto articleDto, List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException, ArticleNotFoundException {

        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (articleDto == null) {
            throw new IllegalArgumentException("Updated article cannot be null");
        }

        Article article = checkArticlePresentAndGet(id);

        List<String> imageLinks = fileManagerUtil.saveFilesAndGetLinks(images);
        if (imageLinks != null && !imageLinks.isEmpty()) {
            article.getImageLinks().addAll(imageLinks);
        }

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());

        return new ArticleDto(articleRepository.save(article));
    }

    @Override
    public void deleteArticle(Long id, User author)
            throws IllegalArgumentException, FileManagerException, ArticleNotFoundException {

        Article article = checkArticlePresentAndGet(id);

        fileManagerUtil.deleteFiles(article.getImageLinks());

        articleRepository.delete(article);
    }

    @Override
    public Page<ArticleDto> getArticlesBySubscription(User user, String sortType, int page, int pageSize)
            throws IllegalArgumentException {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Pageable pageable = PageableUtil.validPaginationAndGetPageable(sortType, page, pageSize, "creationDate");
        Page<Article> articles = articleRepository.findArticlesBySubscribedUserSortedByDate(user.getId(), pageable);

        return articles.map(ArticleDto::new);
    }

    @Override
    public User getArticleAuthorByArticleId(Long id) throws IllegalArgumentException {
        Article article = checkArticlePresentAndGet(id);
        return article.getAuthor();
    }

    private Article checkArticlePresentAndGet(Long id) throws ArticleNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Article id cannot be null");
        }

        Article article = articleRepository.findById(id).orElse(null);

        if (article == null) {
            throw new ArticleNotFoundException("Article not found");
        }

        return article;
    }
}
