package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getCommentsByAuthor(@PathVariable("authorId") Long authorId) {
        //todo to do!
        return null;
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable("articleId") Long articleId) {
        //todo to do!
        return null;
    }
}
