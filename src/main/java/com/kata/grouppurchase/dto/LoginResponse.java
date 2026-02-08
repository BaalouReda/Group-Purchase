package com.kata.grouppurchase.dto;

public record LoginResponse(
        String message,
        String email,
        String role
) {
}
