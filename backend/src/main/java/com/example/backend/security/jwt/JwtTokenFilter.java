package com.example.backend.security.jwt;

import com.example.backend.exception.JwtAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtProvider;

    private final JwtUtils jwtUtils;

    @Value("${jwt.cookieName}")
    private String authCookieName;

    @Autowired
    public JwtTokenFilter(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          JwtTokenProvider jwtProvider,
                          JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtProvider = jwtProvider;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (token != null && jwtProvider.validateJwtToken(token)) {
                String username = jwtProvider.getUserNameFromJwtToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            Cookie cookie = jwtUtils.clearCookie();
            response.addCookie(cookie);

            LOGGER.error("User is trying to log in with invalid or expired Jwt token! Exception: " + e.getMessage());
            throw new JwtAuthenticationException("Jwt token is expired or invalid", HttpStatus.FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(authCookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
