package com.example.backend.service;

import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;

public interface UserService {
    User saveUser(NewUserDto newUserDto) throws IllegalArgumentException;

    User getUserFromUserDetails(UserDetailsImpl authenticatedUser) throws IllegalArgumentException;

    User findUserById(Long id) throws IllegalArgumentException, UserNotFoundException;

    User updateUser(User user) throws IllegalArgumentException;

    void checkEmailExists(String email) throws IllegalArgumentException, UserAlreadyExistsException;

    void checkNameExists(String name) throws IllegalArgumentException, UserAlreadyExistsException;

    UserDto getUserDtoByEmail(String email) throws IllegalArgumentException, UserNotFoundException;
}
