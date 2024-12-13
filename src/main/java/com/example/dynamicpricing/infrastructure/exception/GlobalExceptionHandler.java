package com.example.dynamicpricing.infrastructure.exception;

import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String FORMAT_ISO_DATE_TIME_E_G_2020_06_14_T_22_00_00_Z =
            "Invalid value provided for '%s'. Expected format: ISO_DATE_TIME (e.g., 2020-06-14T22:00:00Z).";
    private static final String NO_ERROR_MESSAGE_PROVIDED = "No error message provided";
    private static final String VALIDATION_FAILED_WITH_ERRORS = "Validation failed with errors: {}";
    private static final String FAILED_TO_CONVERT = "Failed to convert";
    private static final String PRICE_NOT_FOUND_FOR_PRODUCT = "Price not found for product: {}";
    private static final String PRICE_NOT_FOUND = "Price not found";

    @ExceptionHandler(PriceNotAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(PriceNotAvailableException ex) {
        logger.error(PRICE_NOT_FOUND_FOR_PRODUCT, ex.getMessage(), ex);
        final ApiErrorResponse errorResponse = new ApiErrorResponse(PRICE_NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            final String field = error.getField();
            String defaultMessage = error.getDefaultMessage();

            if (defaultMessage != null && defaultMessage.contains(FAILED_TO_CONVERT)) {
                defaultMessage = String.format(FORMAT_ISO_DATE_TIME_E_G_2020_06_14_T_22_00_00_Z, field);
            } else if (defaultMessage == null) {
                defaultMessage = NO_ERROR_MESSAGE_PROVIDED;
            }

            errors.put(field, defaultMessage);
        });
        logger.warn(VALIDATION_FAILED_WITH_ERRORS, errors);
        return ResponseEntity.badRequest().body(errors);
    }
}
