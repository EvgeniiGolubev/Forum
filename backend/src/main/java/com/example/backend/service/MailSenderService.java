package com.example.backend.service;

import com.example.backend.model.entity.user.User;

public interface MailSenderService {
    void sendMessageOnUserEmail(User user, String subject, String message) throws IllegalArgumentException;
}
