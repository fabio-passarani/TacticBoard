package com.tacticboard.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessLogicException extends RuntimeException {
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}