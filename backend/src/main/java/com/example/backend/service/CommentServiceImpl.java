package com.example.backend.service;

import com.example.backend.exception.CommentNotFoundException;
import com.example.backend.model.dto.comment.CommentDto;
import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.comment.Comment;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findCommentsByAuthor(User author) throws IllegalArgumentException {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        return commentRepository.findAllByAuthor(author);
    }

    @Override
    public List<Comment> findCommentsByArticle(Article article) throws IllegalArgumentException {
        if (article == null) {
            throw new IllegalArgumentException("Article cannot be null");
        }

        return commentRepository.findAllByArticle(article);
    }

    @Override
    public Comment createComment(User author, Article article, CommentDto commentDto) throws IllegalArgumentException {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (article == null) {
            throw new IllegalArgumentException("Article cannot be null");
        }

        if (commentDto == null) {
            throw new IllegalArgumentException("New comment cannot be null");
        }

        Comment comment = new Comment(commentDto.getContent(), author, article);

        return commentRepository.save(comment);
    }

    @Override
    public Comment findCommentById(Long commentId) throws CommentNotFoundException, IllegalArgumentException {
        if (commentId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            throw new CommentNotFoundException("Comment not found");
        }

        return comment;
    }

    @Override
    public void deleteComment(Comment comment) throws IllegalArgumentException {
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        commentRepository.delete(comment);
    }
}
