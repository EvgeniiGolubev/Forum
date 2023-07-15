package com.example.backend.repository;

import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.comment.Comment;
import com.example.backend.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor(User author);
    List<Comment> findAllByArticle(Article article);
}
