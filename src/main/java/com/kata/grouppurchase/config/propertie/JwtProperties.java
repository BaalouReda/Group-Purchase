package com.kata.grouppurchase.config.propertie;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        String cookieName,
        long expirationMs
) {
}
