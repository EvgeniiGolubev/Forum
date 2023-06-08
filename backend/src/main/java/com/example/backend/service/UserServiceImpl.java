package com.example.backend.service;

import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.Provider;
import com.example.backend.model.entity.user.Role;
import com.example.backend.model.entity.user.Status;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(NewUserDto newUserDto) throws InvalidParameterException {
        if (newUserDto == null) {
            throw new InvalidParameterException("New user can not be null");
        }

        User user = new User(
                newUserDto.getEmail(),
                passwordEncoder.encode(newUserDto.getPassword()),
                newUserDto.getName(),
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.USER)
        );

        return userRepository.save(user);
    }

    @Override
    public User getUserFromUserDetails(UserDetailsImpl authenticatedUser) throws InvalidParameterException {
        if (authenticatedUser == null) {
            throw new InvalidParameterException("Authenticated user can not be null");
        }

        return userRepository.findByEmail(authenticatedUser.getUsername());
    }

    @Override
    public User findUserById(Long id) throws InvalidParameterException, UserNotFoundException {
        if (id == null) {
            throw new InvalidParameterException("Id can not be null");
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public User updateUser(User user) throws InvalidParameterException {
        if (user == null) {
            throw new InvalidParameterException("Updated user can not be null");
        }

        return userRepository.save(user);
    }

    @Override
    public void checkEmailExists(String email) throws UserAlreadyExistsException, InvalidParameterException {
        if (email == null) {
            throw new InvalidParameterException("Email can not be null");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email is already taken");
        }
    }

    @Override
    public void checkNameExists(String name) throws UserAlreadyExistsException, InvalidParameterException {
        if (name == null) {
            throw new InvalidParameterException("Name can not be null");
        }

        if (userRepository.findByName(name) != null) {
            throw new UserAlreadyExistsException("Name is already taken");
        }
    }

    @Override
    public UserDto getUserDtoByEmail(String email) throws UserNotFoundException, InvalidParameterException {
        if (email == null) {
            throw new InvalidParameterException("Email can not be null");
        }
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return new UserDto(user);
    }
}
