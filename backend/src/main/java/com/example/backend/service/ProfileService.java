package com.example.backend.service;

import com.example.backend.exception.FileManagerException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.dto.user.ChangeUserEmailDto;
import com.example.backend.model.dto.user.ChangeUserPasswordDto;
import com.example.backend.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    UserProfileDto getUserProfileById(Long id) throws IllegalArgumentException, UserNotFoundException;

    UserProfileDto getUserProfile(User owner) throws IllegalArgumentException;

    UserProfileDto updateUserProfile(User owner, UserProfileDto user, MultipartFile image)
            throws IllegalArgumentException, FileManagerException;

    void deleteUserProfile(User owner) throws IllegalArgumentException;

    List<UserProfileDto> getUserSubscriptions(User owner) throws IllegalArgumentException;

    void changeSubscription(User channel, User subscriber, Boolean subscriptionStatus)
            throws IllegalArgumentException, UserNotFoundException;

    List<UserProfileDto> getUserSubscribers(User owner) throws IllegalArgumentException;

    void changeSubscriberStatus(User subscriber, User channel, Boolean subscriberStatus)
            throws IllegalArgumentException, UserNotFoundException;

    User changeUserEmail(User user, ChangeUserEmailDto newEmail) throws IllegalArgumentException;

    void changeUserPassword(User user, ChangeUserPasswordDto newPassword) throws IllegalArgumentException;
}
