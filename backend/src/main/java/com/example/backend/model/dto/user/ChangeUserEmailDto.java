package com.example.backend.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ChangeUserEmailDto implements Serializable {
    @JsonProperty("email")
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
