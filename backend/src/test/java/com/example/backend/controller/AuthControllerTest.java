package com.example.backend.controller;

import com.example.backend.exception.UserAuthenticationException;
import com.example.backend.model.dto.user.LoginUserDto;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletResponse response;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserService userService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUserWithValidFieldsInLoginUserDto() {
        LoginUserDto user = new LoginUserDto();
        user.setEmail("test@mail.ru");
        user.setPassword("password");

        UserDto userDto = new UserDto();

        Set<ConstraintViolation<LoginUserDto>> violations = validator.validate(user);

        when(userService.getUserDtoByEmail(user.getEmail())).thenReturn(userDto);

        ResponseEntity<?> responseEntity = authController.loginUser(user, response);

        assertThat(violations).isEmpty();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDto, responseEntity.getBody());
    }

    @Test
    void loginUserIfLoginUserDtoIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authController.loginUser(null, response)
        );

        assertEquals("Login user can not be null", exception.getMessage());
    }

    @Test
    void loginUserIfUserDoesNotExist() {
        LoginUserDto user = new LoginUserDto();
        user.setEmail("test@mail.ru");
        user.setPassword("password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

        UserAuthenticationException exception = assertThrows(
                UserAuthenticationException.class,
                () -> authController.loginUser(user, response)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void registerUserWithValidFieldsInNewUserDto() {
        NewUserDto user = new NewUserDto();
        user.setEmail("test@mail.ru");
        user.setPassword("password");
        user.setConfirmPassword("password");
        user.setName("John Doe");

        UserDto userDto = new UserDto();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(user);

        when(userService.getUserDtoByEmail(user.getEmail())).thenReturn(userDto);

        ResponseEntity<?> responseEntity = authController.registerUser(user, response);

        assertThat(violations).isEmpty();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDto, responseEntity.getBody());
    }

    @Test
    void registerUserIfNewUserDtoIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authController.registerUser(null, response)
        );

        assertEquals("New user can not be null", exception.getMessage());
        verify(userService, never()).checkEmailExists(anyString());
        verify(userService, never()).checkNameExists(anyString());
        verify(userService, never()).saveUser(any(NewUserDto.class));
    }

    @Test
    void validateWrongFieldsInLoginUserDto() {
        LoginUserDto user = new LoginUserDto();
        user.setEmail("invalidemail");
        user.setPassword("");

        Set<ConstraintViolation<LoginUserDto>> violations = validator.validate(user);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email is not correct", "Password cannot be empty");
    }

    @Test
    void validateWrongFieldsInNewUserDto() {
        NewUserDto user = new NewUserDto();
        user.setEmail("invalidemail");
        user.setPassword("password");
        user.setConfirmPassword("anotherPassword");
        user.setName(" ");

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(user);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email is not correct", "Name cannot be empty", "Passwords do not match");
    }
}