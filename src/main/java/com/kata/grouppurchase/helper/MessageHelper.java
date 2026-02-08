package com.kata.grouppurchase.helper;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {
    private final MessageSource messageSource;

    public MessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, null);
        } catch (Exception e) {
            return code;
        }
    }

    public String getMessage(String code, String... args) {
        try {
            return messageSource.getMessage(code, args, null);
        } catch (Exception e) {
            return code;
        }
    }
}