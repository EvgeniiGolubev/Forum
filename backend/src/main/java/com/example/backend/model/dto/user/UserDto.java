package com.example.backend.model.dto.user;

import com.example.backend.model.entity.user.Role;
import com.example.backend.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Set;

public class UserDto implements Serializable {
    private Long id;
    @JsonProperty("email")
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email can not be empty")
    private String email;
    @JsonProperty("name")
    @NotBlank(message = "Name can not be empty")
    private String name;
    @JsonProperty("userPicture")
    private String userPicture;
    private String status;
    private Set<Role> roles;
    private String locale;
    private String provider;

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.userPicture = user.getUserPicture();
        this.locale = user.getLocale();
        this.provider = user.getProvider().name();
        this.status = user.getStatus().name();
        this.roles = user.getRoles();
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    @JsonProperty("local")
    public String getLocale() {
        return locale;
    }

    @JsonIgnore
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @JsonProperty("provider")
    public String getProvider() {
        return provider;
    }

    @JsonIgnore
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonIgnore
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("roles")
    public Set<Role> getRoles() {
        return roles;
    }

    @JsonIgnore
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
