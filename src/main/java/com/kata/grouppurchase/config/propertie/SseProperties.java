package com.kata.grouppurchase.config.propertie;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sse")
public record SseProperties(
      long  timeOut
) {
}
