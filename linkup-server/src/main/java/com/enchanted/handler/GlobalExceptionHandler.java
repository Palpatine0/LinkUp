package com.enchanted.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidInput(HttpMessageNotReadableException ex, WebRequest request) {
        String errorMessage = "Invalid input format: " + ex.getLocalizedMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
   