package com.example.backend.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMessage {
    @JsonProperty("message")
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
