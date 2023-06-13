package com.example.backend.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
