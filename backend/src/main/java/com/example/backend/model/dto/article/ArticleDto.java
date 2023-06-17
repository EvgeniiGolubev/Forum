package com.example.backend.model.dto.article;

import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.entity.article.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDto implements Serializable {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    @NotBlank(message = "Title can not be empty")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content can not be empty")
    private String content;

    @JsonProperty("author")
    private UserProfileDto author;

    @JsonProperty("creationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
