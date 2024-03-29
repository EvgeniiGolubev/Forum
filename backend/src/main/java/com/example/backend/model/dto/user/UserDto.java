package com.example.backend.model.dto.user;

import com.example.backend.model.entity.user.enums.Role;
import com.example.backend.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Set;

@Data
public class UserDto implements Serializable {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email can not be empty")
    private String email;

    @JsonProperty("name")
    @NotBlank(message = "Name can not be empty")
    @Length(max = 255)
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "Description can not be empty")
    @Length(max = 600)
    private String description;

    @JsonProperty("userPicture")
    private String userPicture;

    @JsonProperty("status")
    private String status;

    @JsonProperty("roles")
    private Set<Role> roles;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("provider")
    private String provider;

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.description = user.getDescription();
        this.userPicture = user.getUserPicture();
        this.locale = user.getLocale();
        this.provider = user.getProvider().name();
        this.status = user.getStatus().name();
        this.roles = user.getRoles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto userDto)) return false;

        return getId() != null ? getId().equals(userDto.getId()) : userDto.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
