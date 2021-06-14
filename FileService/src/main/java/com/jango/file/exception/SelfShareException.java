package com.jango.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SelfShareException extends RuntimeException {

    public SelfShareException(String message) {
        super(message);
    }
}
