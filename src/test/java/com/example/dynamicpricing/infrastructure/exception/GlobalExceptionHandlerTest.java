package com.example.dynamicpricing.infrastructure.exception;

import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String PRICE_NOT_FOUND = "Price not found";
    private static final String DEFAULT_MESSAGE = "must be positive";
    private static final String CONVERSION_ERROR_MESSAGE = "Failed to convert value for 'applicationDate'";
    private static final String NULL_MESSAGE = null;
    private static final String NO_ERROR_MESSAGE_PROVIDED = "No error message provided";
    private static final String EXPECTED_FORMAT_ISO_DATE_TIME = "Expected format: ISO_DATE_TIME";
    private static final String APPLICATION_DATE = "applicationDate";
    private static final String PRICE_REQUEST = "priceRequest";
    private static final String PRICE_FIELD = "price";

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Test
    void givenPriceNotAvailableException_whenHandlePriceNotAvailable_thenReturnNotFoundResponse() {
        final PriceNotAvailableException exception = new PriceNotAvailableException(PRICE_NOT_FOUND);

        final ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleProductNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(PRICE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(PRICE_NOT_FOUND, response.getBody().getDetails());
    }

    @Test
    void givenMethodArgumentNotValidException_whenHandleValidationExceptions_thenReturnBadRequestResponse() {
        final FieldError fieldError = new FieldError(PRICE_REQUEST, PRICE_FIELD, DEFAULT_MESSAGE);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        final ResponseEntity<Map<String, String>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey(PRICE_FIELD));
        assertEquals(DEFAULT_MESSAGE, response.getBody().get(PRICE_FIELD));
    }

    @Test
    void givenMethodArgumentNotValidExceptionWithConversionError_whenHandleValidationExceptions_thenReturnError() {
        final FieldError fieldError = new FieldError(PRICE_REQUEST, APPLICATION_DATE, CONVERSION_ERROR_MESSAGE);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        final ResponseEntity<Map<String, String>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey(APPLICATION_DATE));
        assertTrue(response.getBody().get(APPLICATION_DATE).contains(EXPECTED_FORMAT_ISO_DATE_TIME));
    }

    @Test
    void givenMethodArgumentNotValidExceptionWithNullMessage_whenHandleValidationExceptions_thenReturnDefaultMessage() {
        final FieldError fieldError = new FieldError(PRICE_REQUEST, APPLICATION_DATE, NULL_MESSAGE);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        final ResponseEntity<Map<String, String>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey(APPLICATION_DATE));
        assertEquals(NO_ERROR_MESSAGE_PROVIDED, response.getBody().get(APPLICATION_DATE));
    }
}
