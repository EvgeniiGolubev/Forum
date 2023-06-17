package com.example.backend.controller;

import com.example.backend.exception.UserAuthenticationException;
import com.example.backend.model.dto.user.LoginUserDto;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.model.request.ConfirmEmailRequest;
import com.example.backend.model.response.ResponseMessage;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.MailSenderService;
import com.example.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.InvalidParameterException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MailSenderService mailSenderService;

    private static final String SUBJECT = "Activation code for registration";
    private static final String MESSAGE = "Hello, %s! \nWelcome to Forum. Please, visit next" +
            " link to confirm your account http://%s/confirm-email?code=%s";

    @Autowired
    public AuthController(
            UserService userService,
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager,
            MailSenderService mailSenderService
    ) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserDto loginUserDto, HttpServletResponse response) {
        if (loginUserDto == null) {
            throw new InvalidParameterException("Login user can not be null");
        }

        User user =  userService.findUserByEmail(loginUserDto.getEmail());

        authenticateUser(loginUserDto.getEmail(), loginUserDto.getPassword(), response);

        log.info("User authenticated");

        UserDto userDto = new UserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody NewUserDto newUserDto, HttpServletResponse response) {
        if (newUserDto == null) {
            throw new InvalidParameterException("New user can not be null");
        }

        userService.checkEmailExists(newUserDto.getEmail());
        userService.checkNameExists(newUserDto.getName());

        User user = userService.saveUser(newUserDto);

        mailSenderService.sendMessageOnUserEmail(user, SUBJECT, MESSAGE);

        log.info("New user registered, waiting email confirmation");

        return new ResponseEntity<>(new ResponseMessage("Confirm email sent successfully, waiting confirmation"), HttpStatus.OK);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> activateUserAccount(@RequestBody ConfirmEmailRequest request)  {
        userService.activateUserEmail(request.getCode());

        return new ResponseEntity<>(new ResponseMessage("Email confirm successfully"), HttpStatus.OK);
    }

    @GetMapping("/oauth2-success")
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal UserDetailsImpl authenticatedUser) {
        User user = userService.getUserFromUserDetails(authenticatedUser);

        log.info("User authenticated with oauth2 successfully");

        UserDto userDto = new UserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    private void authenticateUser(String email, String password, HttpServletResponse response)
            throws UserAuthenticationException, InvalidParameterException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            response.addCookie(jwtUtils.makeCookie(email));
        } catch (AuthenticationException e) {
            throw new UserAuthenticationException("Invalid email or password");
        }
    }
}
