package com.example.backend.model.entity.article;

import com.example.backend.model.entity.user.User;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", length = 5000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User author;

    @Column(name="creation_date", updatable = false)
    private LocalDateTime creationDate;

    @ElementCollection
    @Column(name = "image_links")
    private List<String> imageLinks = new ArrayList<>();

    public Article() {}

    public Article(String title, String content, User author, LocalDateTime creationDate) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;

        return getId() != null ? getId().equals(article.getId()) : article.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
