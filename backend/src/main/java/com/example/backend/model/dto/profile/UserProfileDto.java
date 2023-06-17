package com.example.backend.model.dto.profile;

import com.example.backend.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserProfileDto implements Serializable {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    @NotBlank(message = "Name can not be empty")
    private String name;
    @JsonProperty("userPicture")
    private String userPicture;

    public UserProfileDto() {}

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.userPicture = user.getUserPicture();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfileDto that)) return false;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
