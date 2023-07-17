package com.example.backend.controller;

import com.example.backend.mapper.ArticleMapper;
import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.user.enums.Role;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ArticleService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService, ArticleMapper articleMapper) {
        this.articleService = articleService;
        this.userService = userService;
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllArticles(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("stringSearch") String stringSearch,
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        User user = null;

        if (authenticatedUser != null) {
            user = userService.getUserFromUserDetails(authenticatedUser);
        }

        Page<Article> articles = articleService.findAllArticles(stringSearch, sortType, page, pageSize);

        Page<ArticleDto> articleDtos = articleMapper.toArticleDtoPage(articles, user);
        return new ResponseEntity<>(articleDtos, HttpStatus.OK);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getArticlesByAuthor(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @PathVariable("authorId") Long authorId
    ) {
        User author = userService.findUserById(authorId);
        User user = null;

        if (authenticatedUser != null) {
            user = userService.getUserFromUserDetails(authenticatedUser);
        }

        List<Article> articles = articleService.findAllArticlesByAuthor(author);

        List<ArticleDto> articleDtos = articleMapper.toArticleDtoList(articles, user);
        return new ResponseEntity<>(articleDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @PathVariable("id") Long id
    ) {
        User user = null;

        if (authenticatedUser != null) {
            user = userService.getUserFromUserDetails(authenticatedUser);
        }

        Article article = articleService.findArticleById(id);

        ArticleDto articleDto = articleMapper.toArticleDto(article, user);
        return new ResponseEntity<>(articleDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/activity-feed")
    public ResponseEntity<?> getUserActivityFeed(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("stringSearch") String stringSearch,
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        User user = userService.getUserFromUserDetails(authenticatedUser);

        Page<Article> articles = articleService.findArticlesBySubscription(stringSearch, user, sortType, page, pageSize);

        Page<ArticleDto> articleDtos = articleMapper.toArticleDtoPage(articles, user);
        return new ResponseEntity<>(articleDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createArticle(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @ModelAttribute ArticleDto newArticleDto,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> images
    ) {
        User author = userService.getUserFromUserDetails(authenticatedUser);

        Article article = articleService.createArticle(author, newArticleDto, images);

        ArticleDto articleDto = articleMapper.toArticleDto(article, author);
        return new ResponseEntity<>(articleDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateArticle(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @ModelAttribute ArticleDto updatedArticleDto,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> images
    ) {
        User actualAuthor = articleService.getArticleAuthorByArticleId(id);
        User author = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(actualAuthor, author);

        Article article = articleService.updateArticle(id, author, updatedArticleDto, images);

        ArticleDto articleDto = articleMapper.toArticleDto(article, author);
        return new ResponseEntity<>(articleDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser
    ) {
        User actualAuthor = articleService.getArticleAuthorByArticleId(id);
        User author = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(actualAuthor, author);

        articleService.deleteArticle(id, author);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/like/{id}")
    public ResponseEntity<?> changeLikeStatus(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @PathVariable("id") Long id,
            @RequestParam("liked") Boolean liked
    ) {
        User user =  userService.getUserFromUserDetails(authenticatedUser);

        articleService.changeArticleLikeStatus(id, liked, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void checkAccess(User actualAuthor, User author) throws AccessDeniedException {
        Set<Role> roles = author.getRoles();

        if (!author.equals(actualAuthor) && !roles.contains(Role.ADMIN) && !roles.contains(Role.MODERATOR)) {
            throw new AccessDeniedException("Access denied. Only the author can modify or delete the article");
        }
    }
}
