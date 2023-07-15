package com.example.backend.model.entity.comment;

import com.example.backend.model.entity.article.Article;
import com.example.backend.model.entity.user.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false, updatable = false)
    private Article article;

    public Comment() {}

    public Comment(String content, User author, Article article) {
        this.content = content;
        this.author = author;
        this.article = article;
    }
}
