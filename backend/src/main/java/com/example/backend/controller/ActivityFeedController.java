package com.example.backend.controller;

import com.example.backend.model.dto.article.ArticleDto;
import com.example.backend.model.entity.user.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ArticleService;
import com.example.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-feed")
public class ActivityFeedController {
    private final ArticleService articleService;
    private final UserService userService;

    public ActivityFeedController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserActivityFeed(
            @AuthenticationPrincipal UserDetailsImpl authenticatedUser,
            @RequestParam("sortType") String sortType,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        User user = userService.getUserFromUserDetails(authenticatedUser);

        Page<ArticleDto> articles = articleService.getArticlesBySubscription(user, sortType, page, pageSize);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }
}
