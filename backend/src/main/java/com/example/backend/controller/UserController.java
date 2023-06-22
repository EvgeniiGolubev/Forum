package com.example.backend.controller;

import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.model.response.ResponseMessage;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.MailSenderService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final MailSenderService mailSenderService;

    private static final String SUBJECT = "Changed password";
    private static final String MESSAGE = "Hello, %s! \nYour password changed by administration. Your new password is: ";

    @Autowired
    public UserController(UserService userService, MailSenderService mailSenderService) {
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('MODERATOR')")
    public ResponseEntity<?> getUsers(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "locale", required = false) String locale,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        Page<User> users = userService
                .findFilteredUsers(id, email, name, status, role, locale, provider, sortType, page, pageSize);

        Page<UserDto> usersDto = users.map(UserDto::new);
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('MODERATOR')")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        User user =  userService.findUserById(id);

        UserDto userDto = new UserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserDto userDto
    ) {
        User user = userService.updateUser(id, userDto);

        UserDto updatedUser = new UserDto(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/change-password/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeUserPassword(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser
    ) {
        User changeUser = userService.findUserById(id);
        User user = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(changeUser, user);

        String password = userService.changeUserPassword(changeUser);

        mailSenderService.sendMessageOnUserEmail(changeUser, SUBJECT, MESSAGE + password);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/change-status/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('MODERATOR')")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("isBanned") Boolean isBanned
    ) {
        User changeUser = userService.findUserById(id);
        User user = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(changeUser, user);

        userService.changeUserStatus(changeUser, isBanned);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/change-moderator-role/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeUserModeratorRole(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("isModer") Boolean isModer
    ) {
        User changeUser = userService.findUserById(id);
        User user = userService.getUserFromUserDetails(authenticatedUser);

        checkAccess(changeUser, user);

        userService.changeUserModeratorRole(changeUser, isModer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void checkAccess(User changeUser, User user) throws AccessDeniedException {
        if (changeUser.equals(user)) {
            throw new AccessDeniedException("Access denied. Your cannot modify yourself");
        }
    }
}
