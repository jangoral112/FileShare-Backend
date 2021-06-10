package com.jango.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserIsNotFileOwnerException extends RuntimeException {

    public UserIsNotFileOwnerException(String message) {
        super(message);
    }
}
