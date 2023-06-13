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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUserSuccess() {
        NewUserDto newUser = new NewUserDto();
        newUser.setEmail("test@example.com");
        newUser.setName("John Doe");
        newUser.setPassword("password");
        newUser.setPassword("password");

        User expectedUser = new User(
                "test@example.com",
                "encodedPassword",
                "John Doe",
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.USER)
        );

        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.saveUser(newUser);

        assertEquals(expectedUser, result);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @Test
    void saveUserIfNewUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(null)
        );

        assertEquals("New user can not be null", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void getUserFromUserDetailsSuccess() {
        UserDetailsImpl authenticatedUser = new UserDetailsImpl();
        authenticatedUser.setUsername("test@example.com");
        authenticatedUser.setPassword("encodedPassword");
        authenticatedUser.setAuthorities(Collections.singleton(Role.USER));

        User expectedUser = new User(
                "test@example.com",
                "encodedPassword",
                "John Doe",
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.USER)
        );

        when(userRepository.findByEmail("test@example.com")).thenReturn(expectedUser);

        User result = userService.getUserFromUserDetails(authenticatedUser);

        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    void getUserFromUserDetailsIfAuthenticatedUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserFromUserDetails(null)
        );

        assertEquals("Authenticated user can not be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void findUserByIdSuccess() {
        Long userId = 1L;
        User user = new User(
                "test@example.com",
                "encodedPassword",
                "John Doe",
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.USER)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserByIdIfIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.findUserById(null)
        );

        assertEquals("Id can not be null", exception.getMessage());
        verify(userRepository, never()).findById(any(Long.class));
    }

    @Test
    void findUserByIdIfUserNotFound() {
        Long userId = -1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(userId)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserSuccess() {
        User user = new User(
                "test2@example.com",
                "encodedPassword2",
                "John Doe 2",
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.MODERATOR)
        );

        user.setId(1L);

        UserDto expectedUser = new UserDto(user);

        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

//        UserDto result = userService.updateUser(expectedUser);

//        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(expectedUser.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserIfUpdatedUserIsNull() {
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> userService.updateUser(null)
//        );

//        assertEquals("Updated user can not be null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void checkEmailExistsSuccess() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertDoesNotThrow(() -> userService.checkEmailExists(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void checkEmailExistsIfEmailIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.checkEmailExists(null)
        );

        assertEquals("Email can not be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void checkEmailExistsIfEmailIsTaken() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(new User());

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.checkEmailExists(email)
        );

        assertEquals("Email is already taken", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void checkNameExistsSuccess() {
        String name = "John Doe";
        when(userRepository.findByName(name)).thenReturn(null);

        assertDoesNotThrow(() -> userService.checkNameExists(name));
        verify(userRepository, times(1)).findByName(name);
    }

    @Test
    void checkEmailExistsIfNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.checkNameExists(null)
        );

        assertEquals("Name can not be null", exception.getMessage());
        verify(userRepository, never()).findByName(anyString());
    }

    @Test
    void checkEmailExistsIfNameIsTaken() {
        String name = "John Doe";
        when(userRepository.findByName(name)).thenReturn(new User());

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.checkNameExists(name)
        );

        assertEquals("Name is already taken", exception.getMessage());
        verify(userRepository, times(1)).findByName(name);
    }

    @Test
    void getUserDtoByEmailSuccess() {
        UserDto expected = new UserDto();
        expected.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setProvider(Provider.LOCAL);
        user.setStatus(Status.ACTIVE);

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDto result = new UserDto(userService.findUserByEmail(anyString()));

        assertEquals(expected, result);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void getUserDtoByEmailWhenEmailIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.findUserByEmail(null)
        );

        assertEquals("Email can not be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserDtoByEmailWhenUserNotExist() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserByEmail(anyString())
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}