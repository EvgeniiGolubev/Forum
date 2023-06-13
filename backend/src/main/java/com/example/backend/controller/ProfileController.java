package com.example.backend.controller;

import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.dto.user.ChangeUserEmailDto;
import com.example.backend.model.dto.user.ChangeUserPasswordDto;
import com.example.backend.model.entity.user.Provider;
import com.example.backend.model.entity.user.User;
import com.example.backend.model.response.ResponseMessage;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.MailSenderService;
import com.example.backend.service.ProfileService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetailsImpl authenticatedUser) {
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        UserProfileDto profile = profileService.getUserProfile(owner);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @ModelAttribute UserProfileDto user,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        UserProfileDto updatedProfile = profileService.updateUserProfile(owner, user, image);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserProfile(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            HttpServletResponse response
    ) {
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        profileService.deleteUserProfile(owner);

        logout(response);

        return new ResponseEntity<>(new ResponseMessage("User profile successfully deleted"), HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> getUserSubscriptions(@AuthenticationPrincipal UserDetailsImpl authenticatedUser) {
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        List<UserProfileDto> subscriptions = profileService.getUserSubscriptions(owner);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PostMapping("/subscriptions/{channelId}")
    public ResponseEntity<?> changeSubscription(
            @PathVariable("channelId") Long channelId,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("subscriptionStatus") Boolean subscriptionStatus
    ) {
        User channel = userService.findUserById(channelId);
        User subscriber = userService.getUserFromUserDetails(authenticatedUser);

        profileService.changeSubscription(channel, subscriber, subscriptionStatus);
        return new ResponseEntity<>("Subscription status changed successfully", HttpStatus.OK);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<?> getUserSubscribers(@AuthenticationPrincipal UserDetailsImpl authenticatedUser) {
        User owner = userService.getUserFromUserDetails(authenticatedUser);

        List<UserProfileDto> subscribers = profileService.getUserSubscribers(owner);
        return new ResponseEntity<>(subscribers, HttpStatus.OK);
    }

    @PostMapping("/subscribers/{subscriberId}")
    public ResponseEntity<?> changeSubscriberStatus(
            @PathVariable("subscriberId") Long subscriberId,
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("subscriberStatus") Boolean subscriberStatus
    ) {
        User channel = userService.getUserFromUserDetails(authenticatedUser);
        User subscriber = userService.findUserById(subscriberId);

        profileService.changeSubscriberStatus(subscriber, channel, subscriberStatus);
        return new ResponseEntity<>("Subscriber status changed successfully", HttpStatus.OK);
    }

    @PutMapping("/change-email")
    public ResponseEntity<?> changeUserEmail(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @RequestBody ChangeUserEmailDto newEmail,
            HttpServletResponse response
    ) {
        User user = userService.getUserFromUserDetails(authenticatedUser);

        if (user.getProvider() == Provider.GOOGLE) {
            return new ResponseEntity<>("Users registered through oauth2 cannot change email", HttpStatus.BAD_REQUEST);
        }

        user = profileService.changeUserEmail(user, newEmail);
        mailSenderService.sendMessageOnUserEmail(user, SUBJECT, MESSAGE);

        logout(response);

        return new ResponseEntity<>(
                new ResponseMessage("User logged out. Email successfully sent, waiting for confirmation"),
                HttpStatus.OK
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @Valid @RequestBody ChangeUserPasswordDto newPassword,
            HttpServletResponse response
    ) {
        User user = userService.getUserFromUserDetails(authenticatedUser);

        if (user.getProvider() == Provider.GOOGLE) {
            return new ResponseEntity<>("Users registered through oauth2 cannot change password", HttpStatus.BAD_REQUEST);
        }

        profileService.changeUserPassword(user, newPassword);

        logout(response);

        return new ResponseEntity<>(new ResponseMessage("User logged out. Password successfully changed"), HttpStatus.FORBIDDEN);
    }

    private void logout(HttpServletResponse response) {
        Cookie cookie = jwtUtils.clearCookie();
        response.addCookie(cookie);
        SecurityContextHolder.clearContext();
    }
}
