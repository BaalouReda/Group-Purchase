package com.kata.grouppurchase.service.Impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.kata.grouppurchase.config.propertie.JwtProperties;
import com.kata.grouppurchase.dao.UserEntity;
import com.kata.grouppurchase.dto.LoginRequest;
import com.kata.grouppurchase.dto.LoginResponse;
import com.kata.grouppurchase.helper.JwtHelper;
import com.kata.grouppurchase.repository.UserEntityRepository;
import com.kata.grouppurchase.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserEntityRepository userRepository;
    private final JwtProperties jwtProperties;
    private final  AuthenticationManager authenticationManager;
    private final  JwtHelper jwtHelper;

    public UserServiceImpl(UserEntityRepository userRepository, JwtProperties jwtProperties, @Lazy AuthenticationManager authenticationManager, JwtHelper jwtHelper) {
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }

    @Override
    public void logOut(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.cookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        log.info("Cookie value: {}", cookie.getValue());
        response.addCookie(cookie);
    }


    @Override
    public LoginResponse logIn(LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            UserEntity user = (UserEntity) authentication.getPrincipal();
            log.info("Authentication successful for user: {}", user.getEmail());
            String jwtToken = jwtHelper.generateJwtToken(user.getEmail());

            Cookie cookie = new Cookie(jwtProperties.cookieName(), jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtProperties.expirationMs() / 1000));

            response.addCookie(cookie);

            return new LoginResponse(
                    "Login successful",
                    user.getEmail(),
                    user.getRole()
            );

        } catch (BadCredentialsException e) {
            log.error("Authentication failed for email: {} - {}", loginRequest.email(), e.getMessage());
            throw e;
        }
    }

}
