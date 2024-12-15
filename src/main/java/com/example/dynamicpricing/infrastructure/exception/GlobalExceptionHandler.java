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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String NO_ERROR_MESSAGE_PROVIDED = "No error message provided";
    private static final String VALIDATION_FAILED_WITH_ERRORS = "Validation failed with errors: {}";
    private static final String FAILED_TO_CONVERT = "Failed to convert";
    private static final String PRICE_NOT_FOUND_FOR_PRODUCT = "Price not found for product: {}";
    private static final String PRICE_NOT_FOUND = "Price not found";
    private static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred.";
    private static final String TO_REQUIRED_TYPE = "to required type '([^']*)'";
    private static final String UNKNOWN_TYPE = "unknown type";
    private static final String FORMAT_ISO_DATE_TIME_E_G_2020_06_14_T_22_00_00_Z =
            "Invalid value for '%s'. Expected format: ISO_DATE_TIME (e.g., 2020-06-14T22:00:00Z).";
    private static final String INVALID_VALUE_FOR_S_EXPECTED_A_VALID_INTEGER =
            "Invalid value for '%s'. Expected a valid integer.";
    private static final String INVALID_VALUE_FOR_S_EXPECTED_A_VALID_DECIMAL_NUMBER =
            "Invalid value for '%s'. Expected a valid decimal number.";
    private static final String INVALID_VALUE_FOR_S_EXPECTED_TYPE_S = "Invalid value for '%s'. Expected type: %s.";

    private String extractTargetType(String exceptionMessage) {
        return Optional.ofNullable(exceptionMessage)
                .flatMap(msg -> {
                    final Pattern pattern = Pattern.compile(TO_REQUIRED_TYPE);
                    final Matcher matcher = pattern.matcher(msg);
                    return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
                })
                .orElse(UNKNOWN_TYPE);
    }

    private String formatValidationMessage(String field, String targetType) {
        return switch (targetType) {
            case "java.time.ZonedDateTime" -> String.format(FORMAT_ISO_DATE_TIME_E_G_2020_06_14_T_22_00_00_Z, field);
            case "java.lang.Integer" -> String.format(INVALID_VALUE_FOR_S_EXPECTED_A_VALID_INTEGER, field);
            case "java.lang.Double" -> String.format(INVALID_VALUE_FOR_S_EXPECTED_A_VALID_DECIMAL_NUMBER, field);
            default -> String.format(INVALID_VALUE_FOR_S_EXPECTED_TYPE_S, field, targetType);
        };
    }

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
                final String targetType = extractTargetType(defaultMessage);
                defaultMessage = formatValidationMessage(field, targetType);
            } else if (defaultMessage == null) {
                defaultMessage = NO_ERROR_MESSAGE_PROVIDED;
            }

            errors.put(field, defaultMessage);
        });
        logger.warn(VALIDATION_FAILED_WITH_ERRORS, errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericExceptions(Exception ex) {
        logger.error(UNEXPECTED_ERROR, ex.getMessage(), ex);
        final ApiErrorResponse errorResponse = new ApiErrorResponse(INTERNAL_SERVER_ERROR,
                AN_UNEXPECTED_ERROR_OCCURRED);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
