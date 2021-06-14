package com.jango.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class FileShareAlreadyExistException extends RuntimeException {

    public FileShareAlreadyExistException(String message) {
        super(message);
    }
}
