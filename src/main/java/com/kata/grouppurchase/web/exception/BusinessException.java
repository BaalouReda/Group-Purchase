package com.kata.grouppurchase.web.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String messageCode;
    private final String[] messageArgs;

    public BusinessException(String messageCode, HttpStatus httpStatus) {
        this(messageCode, httpStatus, null);
    }

    public BusinessException(String messageCode, HttpStatus httpStatus, String[] messageArgs) {
        super(messageCode);
        this.httpStatus = httpStatus;
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

    public BusinessException(String messageCode, HttpStatus httpStatus, String[] messageArgs, Throwable cause) {
        super(messageCode, cause);
        this.httpStatus = httpStatus;
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String[] getMessageArgs() {
        return messageArgs;
    }
}
