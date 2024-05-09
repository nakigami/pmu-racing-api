package com.pmu.eventracing.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        // Log the caller details
        String callerInfo = "Caller: " + request.getDescription(true);

        // Log the parameters sent
        String parameters = "Parameters: " + request.getParameterMap();

        // Log the exception to the logs
        String errorMessage = "Validation error occurred: " + ex.getMessage();
        log.error("Validation error occurred. Details:\n{} \n{} \n{}", callerInfo, parameters, errorMessage, ex);

        // Return a validation error response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
}
