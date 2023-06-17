package com.example.backend.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class NewUserDto {
    @JsonProperty("email")
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @JsonProperty("confirmPassword")
    @NotBlank(message = "Repeat password cannot be empty")
    private String confirmPassword;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return password.equals(confirmPassword);
    }
}
