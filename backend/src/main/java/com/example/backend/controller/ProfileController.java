package com.example.backend.controller;

import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.dto.user.ChangeUserEmailDto;
import com.example.backend.model.dto.user.ChangeUserPasswordDto;
import com.example.backend.model.entity.user.enums.Provider;
import com.example.backend.model.entity.user.enums.Role;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.MailSenderService;
import com.example.backend.service.ProfileService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;
    private final MailSenderService mailSenderService;
    private final JwtUtils jwtUtils;

    private static final String SUBJECT = "Activation code for changing email";
    private static final String MESSAGE = "Hello, %s! \nYou try change your email. Please, visit next" +
            " link to confirm changing http://%s/confirm-email?code=%s";

    @Autowired
    public ProfileController(
            ProfileService profileService,
            UserService userService,
            MailSenderService mailSenderService,
            JwtUtils jwtUtils
    ) {
        this.profileService = profileService;
        this.userService = userService;
        this.mailSenderService = mailSenderService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable("id") Long id) {
        UserProfileDto profile = profileService.getUserProfileById(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @RequestBody UserProfileDto profile
    ) {
        User owner = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        UserProfileDto updatedProfile = profileService.updateUserProfile(owner, profile);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/update-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUserImage(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        User owner = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        UserProfileDto updatedProfile = profileService.updateUserImage(owner, image);

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserProfile(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            HttpServletResponse response
    ) {
        User owner = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        profileService.deleteUserProfile(owner);

        logout(response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/subscriptions")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable("id") Long id) {
        User owner = userService.findUserById(id);

        List<UserProfileDto> subscriptions = profileService.getUserSubscriptions(owner);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PostMapping("/{id}/subscriptions/{channelId}")
    public ResponseEntity<?> changeSubscription(
            @PathVariable("id") Long id,
            @PathVariable("channelId") Long channelId,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("subscriptionStatus") Boolean subscriptionStatus
    ) {
        User subscriber = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        User channel = userService.findUserById(channelId);

        profileService.changeSubscription(channel, subscriber, subscriptionStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<?> getUserSubscribers(@PathVariable("id") Long id) {
        User owner = userService.findUserById(id);

        List<UserProfileDto> subscribers = profileService.getUserSubscribers(owner);
        return new ResponseEntity<>(subscribers, HttpStatus.OK);
    }

    @PostMapping("/{id}/subscribers/{subscriberId}")
    public ResponseEntity<?> changeSubscriberStatus(
            @PathVariable("id") Long id,
            @PathVariable("subscriberId") Long subscriberId,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("subscriberStatus") Boolean subscriberStatus
    ) {
        User channel = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        User subscriber = userService.findUserById(subscriberId);

        profileService.changeSubscriberStatus(subscriber, channel, subscriberStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/change-email")
    public ResponseEntity<?> changeUserEmail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @RequestBody ChangeUserEmailDto newEmail,
            HttpServletResponse response
    ) {
        User owner = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        if (owner.getProvider() == Provider.GOOGLE) {
            return new ResponseEntity<>("Users registered through oauth2 cannot change email", HttpStatus.BAD_REQUEST);
        }

        owner = profileService.changeUserEmail(owner, newEmail);
        mailSenderService.sendMessageOnUserEmail(owner, SUBJECT, MESSAGE);

        logout(response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changeUserPassword(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @RequestBody ChangeUserPasswordDto newPassword,
            HttpServletResponse response
    ) {
        User owner = checkAccessAndReturnProfileOwner(id, authenticatedUser);

        if (owner.getProvider() == Provider.GOOGLE) {
            return new ResponseEntity<>("Users registered through oauth2 cannot change password", HttpStatus.BAD_REQUEST);
        }

        profileService.changeUserPassword(owner, newPassword);

        logout(response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void logout(HttpServletResponse response) {
        Cookie cookie = jwtUtils.clearCookie();
        response.addCookie(cookie);
        SecurityContextHolder.clearContext();
    }

    private User checkAccessAndReturnProfileOwner(Long id, UserDetailsImpl authenticatedUser)
            throws AccessDeniedException, IllegalArgumentException, UserNotFoundException {

        User actualOwner = userService.findUserById(id);
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        Set<Role> roles = owner.getRoles();

        if (!owner.equals(actualOwner) && !roles.contains(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied. Only the owner can modify or delete profile");
        }

        return owner;
    }
}
