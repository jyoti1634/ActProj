package com.example.accounting.controller;

// to build custom HTTP responses
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;  
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode; // add this import

// Represents a validation error on one field of a Java object
import org.springframework.validation.FieldError;
// Thrown when @Valid fails in controller methods 
import org.springframework.web.bind.MethodArgumentNotValidException;
// to define global exception handling
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
// Gives request details
import org.springframework.web.context.request.WebRequest;

import com.example.accounting.exception.ResourceNotFoundException;



// to build structured JSON response bodies
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// Global exception handler for REST controllers
@ControllerAdvice
public class RestExceptionHandler extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {
    // Handles IllegalArgumentExceptions and returns a structured error response
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        // builds a response body with error details
        Map<String, Object> body = new HashMap<>();
        // adds timestamp, status, error type, message, and request path to the response body
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        // returns a ResponseEntity with the body and 400 status
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({org.springframework.dao.DataIntegrityViolationException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handles all other exceptions and returns a structured error response
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternal(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles validation errors and returns a structured error response with field-specific messages
 

@Override
protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,   // <-- changed from HttpStatus
        WebRequest request) {

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", status.value()); // works the same
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    body.put("errors", fieldErrors);
    body.put("path", request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(body, headers, status);
}

}
