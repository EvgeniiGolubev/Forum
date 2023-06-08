package com.example.backend.controller;

import com.example.backend.model.dto.ArticleDto;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<?> findAllArticles(
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        Page<ArticleDto> articles = articleService.findAllArticles(sortType, page, pageSize);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findArticleById(@PathVariable("id") Long id) {
        ArticleDto article = articleService.findArticleById(id);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<?> createArticle(
            @AuthenticationPrincipal UserDetailsImpl author,
            @Valid @RequestPart("article") ArticleDto articleDto,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    ) {
        ArticleDto article = articleService.createArticle(author, articleDto, images);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl author,
            @Valid @RequestPart("article") ArticleDto articleDto,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    ) {
        ArticleDto article = articleService.updateArticle(id, author, articleDto, images);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl author
    ) {
        articleService.deleteArticle(id, author);
        return new ResponseEntity<>("Article delete successfully", HttpStatus.OK);
    }
}
