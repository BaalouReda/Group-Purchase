package com.kata.grouppurchase.web.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(
            "error.resource.not.found",
            HttpStatus.NOT_FOUND,
            new String[]{resourceName, identifier}
        );
    }
}
