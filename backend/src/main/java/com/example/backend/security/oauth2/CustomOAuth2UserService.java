package com.example.backend.security.oauth2;

import com.example.backend.model.entity.user.Provider;
import com.example.backend.model.entity.user.Role;
import com.example.backend.model.entity.user.Status;
import com.example.backend.model.entity.user.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User =  super.loadUser(userRequest);

        return returnUser(oAuth2User);
    }

    private DefaultOAuth2User returnUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            User newUser = new User(
                    email,
                    null,
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("picture"),
                    oAuth2User.getAttribute("locale"),
                    Provider.GOOGLE,
                    Status.ACTIVE,
                    Collections.singleton(Role.USER)
            );

            userRepository.save(newUser);

            return new DefaultOAuth2User(Collections.singleton(Role.USER), oAuth2User.getAttributes(), "email");
        }

        return new DefaultOAuth2User(user.getRoles(), oAuth2User.getAttributes(), "email");
    }
}
