package com.example.dynamicpricing.infrastructure.exception;

import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(PriceNotAvailableException ex) {
        final ApiErrorResponse errorResponse = new ApiErrorResponse("Price not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseExceptions(HttpMessageNotReadableException ex) {
        final Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid JSON format: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
