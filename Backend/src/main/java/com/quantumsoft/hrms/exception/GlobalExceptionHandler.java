package com.quantumsoft.hrms.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse> resourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request){
        CustomResponse response = new CustomResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<CustomResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CustomResponse> duplicateResourceException(DuplicateResourceException e, HttpServletRequest request){
        CustomResponse response = new CustomResponse(LocalDateTime.now(), HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<CustomResponse>(response, HttpStatus.CONFLICT);
    }

}
