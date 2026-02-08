package com.kata.grouppurchase.config;

import com.kata.grouppurchase.config.propertie.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private String deadlineExchange;
    private String deadlineRoutingKey;
    private String deadlineQueue;

    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
       this.deadlineExchange = rabbitMQProperties.deadlineExchange();
       this.deadlineRoutingKey = rabbitMQProperties.deadlineRoutingKey();
       this.deadlineQueue = rabbitMQProperties.deadlineQueue();
    }

    @Bean Queue deadlineQueue() {
        return new Queue(deadlineQueue, true);
    }

     @Bean
    public CustomExchange deadlineExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");

        return new CustomExchange(
                deadlineExchange,
                "x-delayed-message",
                true,
                false,
                args
        );
    }


    @Bean
    public Binding deadlineBinding(Queue deadlineQueue, CustomExchange deadlineExchange) {
        return BindingBuilder.bind(deadlineQueue)
            .to(deadlineExchange)
            .with(deadlineRoutingKey)
            .noargs();
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
