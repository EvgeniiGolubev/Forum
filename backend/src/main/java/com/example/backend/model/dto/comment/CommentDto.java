package com.example.backend.model.dto.comment;

import com.example.backend.model.dto.profile.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class CommentDto implements Serializable {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    @NotBlank(message = "Content can not be empty")
    @Length(max=600)
    private String content;

    @JsonProperty("author")
    private UserProfileDto author;

    public CommentDto() {}

//    public CommentDto(Comment comment) {
//        this.id = comment.getId();
//        this.content = comment.getContent();
//        this.author = new UserProfileDto(comment.getAuthor());
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto that)) return false;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
