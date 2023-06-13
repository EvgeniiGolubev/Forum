package com.example.backend.model.dto.article;

import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.entity.article.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ArticleDto implements Serializable {
    private Long id;

    @JsonProperty("title")
    @NotBlank(message = "Title can not be empty")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content can not be empty")
    private String content;

    private UserProfileDto author;

    private LocalDateTime creationDate;

    @JsonProperty("imageLinks")
    private List<String> imageLinks;

    public ArticleDto() {}

    public ArticleDto(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = new UserProfileDto(article.getAuthor());
        this.creationDate = article.getCreationDate();
        this.imageLinks = article.getImageLinks();
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("author")
    public UserProfileDto getAuthor() {
        return author;
    }

    @JsonIgnore
    public void setAuthor(UserProfileDto author) {
        this.author = author;
    }

    @JsonProperty("creationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleDto that)) return false;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
