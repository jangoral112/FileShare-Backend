package com.jango.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileKeyDoesNotExistException extends RuntimeException {

    public FileKeyDoesNotExistException(String message) {
        super(message);
    }
}
