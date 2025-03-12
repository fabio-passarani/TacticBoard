package com.tacticboard.api.advice;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return ResponseBuilder.error(HttpStatus.UNAUTHORIZED, "Invalid credentials", request.getDescription(false));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ResponseBuilder.error(HttpStatus.FORBIDDEN, "Access denied", request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getDescription(false));
    }
}