package com.example.backend.service;

import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;

import java.security.InvalidParameterException;

public interface UserService {
    User saveUser(NewUserDto newUserDto) throws InvalidParameterException;

    User getUserFromUserDetails(UserDetailsImpl authenticatedUser) throws InvalidParameterException;

    User findUserById(Long id) throws InvalidParameterException, UserNotFoundException;

    User updateUser(User user) throws InvalidParameterException;

    void checkEmailExists(String email) throws InvalidParameterException, UserAlreadyExistsException;

    void checkNameExists(String name) throws InvalidParameterException, UserAlreadyExistsException;

    UserDto getUserDtoByEmail(String email) throws UserNotFoundException, InvalidParameterException;
}
