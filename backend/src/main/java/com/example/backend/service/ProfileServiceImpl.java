package com.example.backend.service;

import com.example.backend.exception.FileManagerException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.profile.UserProfileDto;
import com.example.backend.model.dto.user.ChangeUserEmailDto;
import com.example.backend.model.dto.user.ChangeUserPasswordDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.model.entity.user.UserSubscription;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStoreService fileStoreService;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    public ProfileServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            FileStoreService fileStoreService,
            UserSubscriptionRepository userSubscriptionRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStoreService = fileStoreService;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Override
    public UserProfileDto getUserProfileById(Long id) throws IllegalArgumentException, UserNotFoundException {
        if (id == null) {
            throw new UserNotFoundException("User id cannot be null");
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return new UserProfileDto(user);
    }

    @Override
    public UserProfileDto getUserProfile(User owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new UserNotFoundException("User id cannot be null");
        }

        return new UserProfileDto(owner);
    }

    @Override
    public UserProfileDto updateUserProfile(User owner, UserProfileDto updateUser) throws IllegalArgumentException {

        if (owner == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (updateUser == null) {
            throw new IllegalArgumentException("Updated user cannot be null");
        }



        owner.setName(updateUser.getName());
        owner.setDescription(updateUser.getDescription());

        return new UserProfileDto(userRepository.save(owner));
    }

    @Override
    public void updateUserImage(User owner, MultipartFile image) throws FileManagerException {
        if (image != null) {
            List<String> links = fileStoreService.saveFiles(List.of(image));

            if (links != null && !links.isEmpty()) {
                owner.setUserPicture(links.get(0));
            }
        }
    }

    @Override
    public void deleteUserProfile(User owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        userRepository.delete(owner);
    }

    @Override
    public List<UserProfileDto> getUserSubscriptions(User owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userSubscriptionRepository.findBySubscriber(owner).stream()
                .map(sub -> new UserProfileDto(sub.getChannel()))
                .collect(Collectors.toList());
    }

    @Override
    public void changeSubscription(User channel, User subscriber, Boolean subscriptionStatus)
            throws IllegalArgumentException, UserNotFoundException {

        if (channel == null) {
            throw new UserNotFoundException("Channel cannot be null");
        }

        if (subscriber == null) {
            throw new UserNotFoundException("Subscriber cannot be null");
        }

        if (subscriptionStatus == null) {
            throw new IllegalArgumentException("Subscription status cannot be null");
        }

        UserSubscription subscriptions = userSubscriptionRepository.findByChannelAndSubscriber(channel, subscriber);;

        if (subscriptionStatus && subscriptions == null) {
            subscriptions = new UserSubscription(channel, subscriber);
            userSubscriptionRepository.save(subscriptions);
        } else if (!subscriptionStatus && subscriptions != null) {
            userSubscriptionRepository.delete(subscriptions);
        }
    }

    @Override
    public List<UserProfileDto> getUserSubscribers(User owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userSubscriptionRepository.findByChannel(owner).stream()
                .map(sub -> new UserProfileDto(sub.getSubscriber()))
                .collect(Collectors.toList());
    }

    @Override
    public void changeSubscriberStatus(User subscriber, User channel, Boolean subscriberStatus)
            throws IllegalArgumentException, UserNotFoundException {
        if (channel == null) {
            throw new UserNotFoundException("Channel cannot be null");
        }

        if (subscriber == null) {
            throw new UserNotFoundException("Subscriber cannot be null");
        }

        if (subscriberStatus == null) {
            throw new IllegalArgumentException("Subscriber status cannot be null");
        }

        UserSubscription unansweredSubscription = userSubscriptionRepository.findByChannelAndSubscriber(channel, subscriber);

        UserSubscription reverseSubscription = userSubscriptionRepository.findByChannelAndSubscriber(subscriber,channel);

        if (subscriberStatus) {
            unansweredSubscription.setActive(true);
            userSubscriptionRepository.save(unansweredSubscription);

            if (reverseSubscription == null) {
                reverseSubscription = new UserSubscription(subscriber, channel);
                reverseSubscription.setActive(true);
                userSubscriptionRepository.save(reverseSubscription);
            }

        } else {
            unansweredSubscription.setActive(false);
            userSubscriptionRepository.save(unansweredSubscription);

            if (reverseSubscription != null) {
                userSubscriptionRepository.delete(reverseSubscription);
            }
        }
    }

    @Override
    public User changeUserEmail(User user, ChangeUserEmailDto newEmail) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (newEmail == null) {
            throw new IllegalArgumentException("New email cannot be null");
        }

        if (user.getEmail().equals(newEmail.getEmail()) && passwordEncoder.matches(newEmail.getPassword(), user.getPassword())) {
            user.setEmail(newEmail.getEmail());
            user.setActive(false);
            user.setActivationCode(UUID.randomUUID().toString());

            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid email or password fields");
        }
    }

    @Override
    public void changeUserPassword(User user, ChangeUserPasswordDto userPasswordDto) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (userPasswordDto == null) {
            throw new IllegalArgumentException("New password cannot be null");
        }

        if (passwordEncoder.matches(userPasswordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userPasswordDto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Password mismatch");
        }
    }
}
