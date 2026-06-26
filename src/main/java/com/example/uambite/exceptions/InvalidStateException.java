package com.example.uambite.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends BusinessException {

    public InvalidStateException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_STATE");
    }
}
