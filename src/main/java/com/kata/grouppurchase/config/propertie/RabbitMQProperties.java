package com.kata.grouppurchase.config.propertie;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(
        String deadlineQueue,
        String deadlineExchange,
        String deadlineRoutingKey
) {
}
