package com.school.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import com.school.school.response.ResponseWrapper;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleGenericException(Exception e) {
        log.error("Global error handler caught: ", e);

        // Standard response for uncaught exception
        ResponseWrapper<Object> errorResponse = new ResponseWrapper<>("An unexpected error occurred. Please try again later.", false);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedRoleActionException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleUnauthorizedRoleActionException(UnauthorizedRoleActionException e) {
        log.error("IMPORTANT: Global error handler caught: ", e);

        // Standard response
        ResponseWrapper<Object> errorResponse = new ResponseWrapper<>(e.getMessage(), false);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}