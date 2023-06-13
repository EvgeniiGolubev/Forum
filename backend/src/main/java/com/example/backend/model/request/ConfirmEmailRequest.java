package com.example.backend.model.request;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ConfirmEmailRequest implements Serializable {

    @NotBlank(message = "Code can not be empty")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
