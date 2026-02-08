package com.kata.grouppurchase.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.kata.grouppurchase.dto.LoginRequest;
import com.kata.grouppurchase.dto.LoginResponse;
import com.kata.grouppurchase.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public record AuthController(
        UserService  userService
) {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        log.info("Login attempt for email: {}", loginRequest.email());
        LoginResponse loginResponse =  userService.logIn(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        userService.logOut(response);
        log.debug("logout successful");
        return ResponseEntity.noContent().build();
    }
}
