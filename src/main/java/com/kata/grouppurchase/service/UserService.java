package com.kata.grouppurchase.service;

import jakarta.servlet.http.HttpServletResponse;
import com.kata.grouppurchase.dto.LoginRequest;
import com.kata.grouppurchase.dto.LoginResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void logOut(HttpServletResponse response);

    LoginResponse logIn(LoginRequest loginRequest, HttpServletResponse response);
}
