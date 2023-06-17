package com.example.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ConfirmEmailRequest implements Serializable {

    @NotBlank(message = "Code can not be empty")
    private String code;
}
