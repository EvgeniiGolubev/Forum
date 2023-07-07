package com.example.backend.controller;

import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.user.Role;
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
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> findAllArticles(
            @RequestParam("stringSearch") String stringSearch,
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        Page<ArticleDto> articles = articleService.findAllArticles(stringSearch, sortType, page, pageSize);

        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> findArticlesByAuthor(@PathVariable("authorId") Long authorId) {
        //todo to do!
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findArticleById(@PathVariable("id") Long id) {
        ArticleDto article = articleService.findArticleById(id);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createArticle(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @ModelAttribute ArticleDto articleDto,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> images
    ) {
        User author = userService.getUserFromUserDetails(authenticatedUser);

        ArticleDto article = articleService.createArticle(author, articleDto, images);
        return new ResponseEntity<>(article, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateArticle(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @ModelAttribute ArticleDto articleDto,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> images
    ) {
        User actualAuthor = articleService.getArticleAuthorByArticleId(id);
        User author = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(actualAuthor, author);

        ArticleDto article = articleService.updateArticle(id, author, articleDto, images);
        return new ResponseEntity<>(article, HttpStatus.OK);
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

    private void checkAccess(User actualAuthor, User author) throws AccessDeniedException {
        Set<Role> roles = author.getRoles();

        if (!author.equals(actualAuthor) && !roles.contains(Role.ADMIN) && !roles.contains(Role.MODERATOR)) {
            throw new AccessDeniedException("Access denied. Only the author can modify or delete the article");
        }
    }
}
