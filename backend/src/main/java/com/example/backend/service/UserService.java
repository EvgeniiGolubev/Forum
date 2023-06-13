package com.example.backend.service;

import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<User> findFilteredUsers(
            Long id, String email, String name,
            String status, String role, String locale,
            String provider, String sortType, int page, int pageSize
    ) throws IllegalArgumentException;

    User findUserById(Long id) throws IllegalArgumentException, UserNotFoundException;

    User findUserByEmail(String email) throws IllegalArgumentException, UserNotFoundException;

    User saveUser(NewUserDto newUserDto) throws IllegalArgumentException;

    User updateUser(Long id, UserDto user) throws IllegalArgumentException, UserNotFoundException;

    void deleteUser(Long id) throws IllegalArgumentException, UserNotFoundException;

    User getUserFromUserDetails(UserDetailsImpl authenticatedUser) throws IllegalArgumentException;

    void checkEmailExists(String email) throws IllegalArgumentException, UserAlreadyExistsException;

    void checkNameExists(String name) throws IllegalArgumentException, UserAlreadyExistsException;

    void activateUserEmail(String code) throws IllegalArgumentException, UserNotFoundException;

    void changeUserStatus(User changeUser, Boolean isBanned) throws IllegalArgumentException;

    String changeUserPassword(User changeUser) throws IllegalArgumentException;

    void changeUserModeratorRole(User changeUser, Boolean isModer) throws IllegalArgumentException;
}
