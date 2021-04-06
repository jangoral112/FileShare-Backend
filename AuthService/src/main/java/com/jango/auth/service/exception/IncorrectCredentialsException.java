package com.jango.auth.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class IncorrectCredentialsException extends RuntimeException {

    public IncorrectCredentialsException(String message) {
        super(message);
    }
}
