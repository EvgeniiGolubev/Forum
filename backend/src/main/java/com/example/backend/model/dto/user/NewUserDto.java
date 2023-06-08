package com.example.backend.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

public class NewUserDto extends AuthUserDto {
    @JsonProperty("confirmPassword")
    @NotBlank(message = "Repeat password cannot be empty")
    private String confirmPassword;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return getPassword().equals(getConfirmPassword());
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
