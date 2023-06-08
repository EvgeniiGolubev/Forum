package com.example.backend.service;

import com.example.backend.exception.ArticleNotFoundException;
import com.example.backend.exception.FileManagerException;
import com.example.backend.exception.UserAuthenticationException;
import com.example.backend.model.dto.ArticleDto;
import com.example.backend.model.entity.Article;
import com.example.backend.model.entity.user.Role;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.ArticleRepository;
import com.example.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Value("${upload.path}")
    private String uploadPath;
    private final ArticleRepository articleRepository;
    private final UserService userService;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UserService userService) {
        this.articleRepository = articleRepository;
        this.userService = userService;
    }

    @Override
    public Page<ArticleDto> findAllArticles(String sortType, int page, int pageSize) {
        Pageable pageable = validPaginationAndGetPageable(sortType, page, pageSize);
        Page<Article> articles = articleRepository.findAll(pageable);
        return articles.map(ArticleDto::new);
    }

    @Override
    public ArticleDto findArticleById(Long id) throws IllegalArgumentException, ArticleNotFoundException {
        Article article = checkArticlePresentAndGet(id);

        return new ArticleDto(article);
    }

    @Override
    public ArticleDto createArticle(UserDetailsImpl authenticatedUser, ArticleDto articleDto, List<MultipartFile> images)
            throws IllegalArgumentException, UserAuthenticationException, FileManagerException {

        if (authenticatedUser == null) {
            throw new UserAuthenticationException("Only authenticated users can create article");
        }

        if (articleDto == null) {
            throw new IllegalArgumentException("New article can not be null");
        }

        User author = userService.getUserFromUserDetails(authenticatedUser);
        List<String> imageLinks = saveFilesAndGetLinks(images);

        Article newArticle = new Article(
                articleDto.getTitle(),
                articleDto.getContent(),
                author,
                LocalDateTime.now()
        );

        if (imageLinks != null) {
            newArticle.getImageLinks().addAll(imageLinks);
        }

        return new ArticleDto(articleRepository.save(newArticle));
    }

    @Override
    public ArticleDto updateArticle(
            Long id,
            UserDetailsImpl authenticatedUser,
            ArticleDto articleDto,
            List<MultipartFile> images
    ) throws
            AccessDeniedException,
            IllegalArgumentException,
            UserAuthenticationException,
            FileManagerException,
            ArticleNotFoundException {

        if (authenticatedUser == null) {
            throw new UserAuthenticationException("Only authenticated users can update article");
        }

        if (articleDto == null) {
            throw new IllegalArgumentException("Updated article can not be null");
        }

        Article article = checkArticlePresentAndGet(id);

        checkAccess(article, authenticatedUser);

        List<String> imageLinks = saveFilesAndGetLinks(images);
        if (imageLinks != null) {
            article.getImageLinks().addAll(imageLinks);
        }

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());

        return new ArticleDto(articleRepository.save(article));
    }

    @Override
    public void deleteArticle(
            Long id,
            UserDetailsImpl authenticatedUser
    ) throws
            AccessDeniedException,
            IllegalArgumentException,
            UserAuthenticationException,
            FileManagerException,
            ArticleNotFoundException {

        if (authenticatedUser == null) {
            throw new UserAuthenticationException("Only authenticated users can delete article");
        }

        Article article = checkArticlePresentAndGet(id);

        checkAccess(article, authenticatedUser);

        deleteFiles(article.getImageLinks());
    }

    private void checkAccess(Article article, UserDetailsImpl authenticatedUser) throws AccessDeniedException {
        User user = userService.getUserFromUserDetails(authenticatedUser);
        User author = article.getAuthor();

        Set<Role> roles = author.getRoles();

        if (!user.equals(author) && !roles.contains(Role.ADMIN) || !roles.contains(Role.MODERATOR)) {
            throw new AccessDeniedException("Access denied. Only the author can modify or delete the article");
        }
    }

    private Article checkArticlePresentAndGet(Long id) throws ArticleNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Article id can not be null");
        }

        Article article = articleRepository.findById(id).orElse(null);

        if (article == null) {
            throw new ArticleNotFoundException("Article not found");
        }

        return article;
    }

    private Pageable validPaginationAndGetPageable(String sortType, int page, int pageSize) {
        if (sortType == null) {
            throw new IllegalArgumentException("Sort type cannot be null");
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }

        if (pageSize < 0) {
            throw new IllegalArgumentException("Page size must be non-negative");
        }

        Pageable pageable;
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType), "createDate");
            pageable = PageRequest.of(page, pageSize, sort);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sortType value! Must be 'DESC' or 'ASC");
        }

        return pageable;
    }

    private List<String> saveFilesAndGetLinks(List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException {

        if (images == null || images.isEmpty()) {
            return null;
        }

        List<String> links = new ArrayList<>();

        try {
            Path path = Paths.get(uploadPath);
            Files.createDirectories(path);

            for (MultipartFile file : images) {
                String uuidFileName = UUID.randomUUID().toString();
                String originFileName = file.getOriginalFilename();

                if (originFileName == null) {
                    continue;
                }

                String extension = originFileName.substring(originFileName.lastIndexOf(".")).toLowerCase();

                if (!extension.matches("\\.(jpg|jpeg|png)")) {
                    throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG formats are allowed");
                }

                String resultFileName = uuidFileName + extension;
                Path filePath = Paths.get(uploadPath, resultFileName);
                file.transferTo(Files.createFile(filePath));

                links.add(resultFileName);
            }

            return links;

        } catch (Exception e) {

            throw new FileManagerException("An error occurred while saving files");
        }
    }

    private void deleteFiles(List<String> imageLinks) throws FileManagerException {
        if (imageLinks == null) {
            return;
        }

        try {
            for (String link : imageLinks) {
                Path filePath = Paths.get(uploadPath, link);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (Exception e) {

            throw new FileManagerException("An error occurred while deleting files");
        }
    }
}
