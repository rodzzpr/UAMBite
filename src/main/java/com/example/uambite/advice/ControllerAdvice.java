package com.example.uambite.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> getErrorValidation(MethodArgumentNotValidException ex)
    {
        Map<String, String> errorValidation = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errorValidation.put("error", error.getDefaultMessage())
        );
        return errorValidation;
    }
}
