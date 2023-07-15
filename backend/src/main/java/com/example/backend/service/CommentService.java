package com.example.backend.service;

import com.example.backend.exception.CommentNotFoundException;
import com.example.backend.model.dto.comment.CommentDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.comment.Comment;
import com.example.backend.model.entity.user.User;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByAuthor(User author) throws IllegalArgumentException;
    List<Comment> findCommentsByArticle(Article article) throws IllegalArgumentException;
    Comment createComment(User author, Article article, CommentDto commentDto) throws IllegalArgumentException;

    Comment findCommentById(Long commentId) throws CommentNotFoundException, IllegalArgumentException;

    void deleteComment(Comment comment) throws IllegalArgumentException;
}
