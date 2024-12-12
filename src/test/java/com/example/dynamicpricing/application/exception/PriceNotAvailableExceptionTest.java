package com.example.dynamicpricing.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceNotAvailableExceptionTest {

    private static final String EXPECTED_MESSAGE = "Price not available for the requested product.";
    private static final String NULL_MESSAGE = null;

    @Test
    void givenMessage_whenCreatingException_thenMessageIsSetCorrectly() {
        final PriceNotAvailableException exception = new PriceNotAvailableException(EXPECTED_MESSAGE);

        assertEquals(EXPECTED_MESSAGE, exception.getMessage());
    }

    @Test
    void givenNullMessage_whenCreatingException_thenMessageIsNull() {
        final PriceNotAvailableException exception = new PriceNotAvailableException(NULL_MESSAGE);

        assertNull(exception.getMessage());
    }

    @Test
    void givenException_whenThrowingAndCatching_thenExceptionIsOfCorrectType() {
        try {
            throw new PriceNotAvailableException(EXPECTED_MESSAGE);
        } catch (PriceNotAvailableException ex) {
            assertEquals(EXPECTED_MESSAGE, ex.getMessage());
            assertInstanceOf(RuntimeException.class, ex);
        }
    }
}

