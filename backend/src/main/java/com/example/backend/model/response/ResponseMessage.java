package com.example.backend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseMessage {
    @JsonProperty("message")
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }
}
