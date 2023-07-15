package com.example.backend.controller;

import com.example.backend.model.dto.comment.CommentDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.comment.Comment;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ArticleService;
import com.example.backend.service.CommentService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    @Autowired
    public CommentController(UserService userService, ArticleService articleService, CommentService commentService) {
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getCommentsByAuthor(@PathVariable("authorId") Long authorId) {
        User author = userService.findUserById(authorId);

        List<CommentDto> comments = commentService.findCommentsByAuthor(author)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable("articleId") Long articleId) {
        Article article = articleService.findArticleById(articleId);

        List<CommentDto> comments = commentService.findCommentsByArticle(article)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentBuId(@PathVariable("commentId") Long commentId) {
        Comment comment = commentService.findCommentById(commentId);
        CommentDto commentDto = new CommentDto(comment);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @PostMapping("/article/{articleId}")
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserDetailsImpl authorisedUser,
            @PathVariable("articleId") Long articleId,
            @Valid @RequestBody CommentDto newComment
    ) {
        User author = userService.getUserFromUserDetails(authorisedUser);
        Article article = articleService.findArticleById(articleId);

        Comment comment = commentService.createComment(author, article, newComment);
        CommentDto commentDto = new CommentDto(comment);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal UserDetailsImpl authorisedUser,
            @PathVariable("commentId") Long commentId
    ) {
        User author = userService.getUserFromUserDetails(authorisedUser);
        Comment comment = commentService.findCommentById(commentId);

        if (author == comment.getAuthor()) {
            throw new AccessDeniedException("Access denied. Only the author can delete the comment");
        }

        commentService.deleteComment(comment);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
