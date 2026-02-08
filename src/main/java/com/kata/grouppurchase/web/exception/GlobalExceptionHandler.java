package com.kata.grouppurchase.web.exception;

import com.kata.grouppurchase.helper.MessageHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageHelper messageHelper;

    public GlobalExceptionHandler(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        String message = messageHelper.getMessage(ex.getMessageCode(), ex.getMessageArgs());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            ex.getHttpStatus(),
            message
        );
        problemDetail.setTitle(ex.getHttpStatus().getReasonPhrase());
        problemDetail.setType(URI.create("about:blank"));

        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + ", " + replacement
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(messageHelper.getMessage("error.validation"));
        problemDetail.setProperty("errors",errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        String message = messageHelper.getMessage("error.internal");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            message
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("about:blank"));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(
            HttpStatus.INTERNAL_SERVER_ERROR
        );
        problemDetail.setTitle("Invalid email or password");
        problemDetail.setType(URI.create("about:blank"));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }
}
