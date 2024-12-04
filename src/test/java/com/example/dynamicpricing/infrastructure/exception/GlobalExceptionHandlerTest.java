package com.example.dynamicpricing.infrastructure.exception;

import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    private static final String NOT_READABLE_MESSAGE = "Invalid JSON format";
    private static final String NOT_READABLE_MESSAGE_TEMPLATE = "Invalid JSON format: Invalid JSON format";

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Test
    void givenPriceNotAvailableException_whenHandlePriceNotAvailable_thenReturnNotFoundResponse() {
        PriceNotAvailableException exception = new PriceNotAvailableException(PRICE_NOT_FOUND);

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleProductNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(PRICE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(PRICE_NOT_FOUND, response.getBody().getDetails());
    }

    @Test
    void givenMethodArgumentNotValidException_whenHandleValidationExceptions_thenReturnBadRequestResponse() {
        FieldError fieldError = new FieldError("priceRequest", "price", DEFAULT_MESSAGE);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("price"));
        assertEquals(DEFAULT_MESSAGE, response.getBody().get("price"));
    }

    @Test
    void givenHttpMessageNotReadableException_whenHandleJsonParseExceptions_thenReturnBadRequestResponse() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(NOT_READABLE_MESSAGE);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleJsonParseExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("error"));
        assertEquals(NOT_READABLE_MESSAGE_TEMPLATE, response.getBody().get("error"));
    }
}

