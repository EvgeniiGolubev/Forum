package com.example.backend.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ChangeUserPasswordDto implements Serializable {
    @JsonProperty("oldPassword")
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    @JsonProperty("newPassword")
    @NotBlank(message = "Password cannot be empty")
    private String newPassword;

    @JsonProperty("confirmPassword")
    @NotBlank(message = "Repeat password cannot be empty")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return newPassword.equals(confirmPassword);
    }
}
