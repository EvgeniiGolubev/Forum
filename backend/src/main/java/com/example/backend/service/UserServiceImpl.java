package com.example.backend.service;

import com.example.backend.exception.ArticleNotFoundException;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.dto.user.NewUserDto;
import com.example.backend.model.dto.user.UserDto;
import com.example.backend.model.entity.user.enums.Provider;
import com.example.backend.model.entity.user.enums.Role;
import com.example.backend.model.entity.user.enums.Status;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.utils.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;

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
    public Page<User> findFilteredUsers(
            Long id, String email, String name,
            String status, String role, String locale,
            String provider, String sortType, int page, int pageSize)
            throws IllegalArgumentException {

        Pageable pageable = PageableUtil.validPaginationAndGetPageable(sortType, page, pageSize, "id");

        return userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            if (status != null && !status.isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("status"), Status.valueOf(status)));
                } catch (IllegalArgumentException ignore) {}
            }

            if (role != null && !role.isEmpty()) {
                try {
                    Join<User, Role> roleJoin = root.join("roles");
                    predicates.add(criteriaBuilder.equal(roleJoin, Role.valueOf(role)));
                } catch (IllegalArgumentException ignore) {}
            }

            if (locale != null && !locale.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("locale"), locale));
            }

            if (provider != null && !provider.isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("provider"), Provider.valueOf(provider)));
                } catch (IllegalArgumentException ignore) {}
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public User findUserById(Long id) throws IllegalArgumentException, UserNotFoundException {
        return checkUserPresentByIdAndGet(id);
    }

    @Override
    public User findUserByEmail(String email) throws IllegalArgumentException, UserNotFoundException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public User saveUser(NewUserDto newUserDto) throws IllegalArgumentException {
        if (newUserDto == null) {
            throw new IllegalArgumentException("New user cannot be null");
        }

        User user = new User(
                newUserDto.getEmail(),
                passwordEncoder.encode(newUserDto.getPassword()),
                newUserDto.getName(),
                null,
                null,
                null,
                Provider.LOCAL,
                Status.ACTIVE,
                Collections.singleton(Role.USER)
        );

        user.setActive(false);
        user.setActivationCode(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) throws IllegalArgumentException, UserNotFoundException {
        if (userDto == null) {
            throw new IllegalArgumentException("Updated user cannot be null");
        }

        User user = checkUserPresentByIdAndGet(id);

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new IllegalArgumentException("You cannot modify administrator");
        }

        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) throws IllegalArgumentException, UserNotFoundException {
        User user = checkUserPresentByIdAndGet(id);

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new IllegalArgumentException("You cannot modify administrator");
        }

        userRepository.delete(user);
    }

    @Override
    public User getUserFromUserDetails(UserDetailsImpl authenticatedUser) throws IllegalArgumentException {
        if (authenticatedUser == null) {
            throw new IllegalArgumentException("Authenticated user cannot be null");
        }

        return userRepository.findByEmail(authenticatedUser.getUsername());
    }

    @Override
    public void checkEmailExists(String email) throws IllegalArgumentException, UserAlreadyExistsException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email is already taken");
        }
    }

    @Override
    public void checkNameExists(String name) throws IllegalArgumentException, UserAlreadyExistsException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (userRepository.findByName(name) != null) {
            throw new UserAlreadyExistsException("Name is already taken");
        }
    }

    @Override
    public void activateUserEmail(String code) throws IllegalArgumentException, UserNotFoundException {
        if (code == null) {
            throw new IllegalArgumentException("Activation code cannot be null");
        }

        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            throw new UserNotFoundException("Activated code is not found");
        }

        user.setActivationCode(null);
        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public void changeUserStatus(User changeUser, Boolean isBanned) throws IllegalArgumentException {
        if (changeUser == null) {
            throw new IllegalArgumentException("Change user cannot be null");
        }

        if (isBanned == null) {
            throw new IllegalArgumentException("isBanned cannot be null");
        }

        if (changeUser.getRoles().contains(Role.ADMIN)) {
            throw new IllegalArgumentException("You cannot modify administrator");
        }

        if (isBanned) {
            changeUser.setStatus(Status.BANNED);
        } else {
            changeUser.setStatus(Status.ACTIVE);
        }

        userRepository.save(changeUser);
    }

    @Override
    public String changeUserPassword(User changeUser) throws IllegalArgumentException {
        if (changeUser == null) {
            throw new IllegalArgumentException("Change user cannot be null");
        }

        if (changeUser.getRoles().contains(Role.ADMIN)) {
            throw new IllegalArgumentException("You cannot modify administrator");
        }

        String password = generatePassword();

        changeUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(changeUser);

        return password;
    }

    @Override
    public void changeUserModeratorRole(User changeUser, Boolean isModer) throws IllegalArgumentException {
        if (changeUser == null) {
            throw new IllegalArgumentException("Change user cannot be null");
        }

        if (isModer == null) {
            throw new IllegalArgumentException("isModer cannot be null");
        }

        if (changeUser.getRoles().contains(Role.ADMIN)) {
            throw new IllegalArgumentException("You cannot modify administrator");
        }

        if (isModer) {
            changeUser.getRoles().add(Role.MODERATOR);
        } else {
            changeUser.getRoles().remove(Role.MODERATOR);
        }

        userRepository.save(changeUser);
    }

    private User checkUserPresentByIdAndGet(Long id) throws ArticleNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    private String generatePassword() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
