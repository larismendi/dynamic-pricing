package com.example.dynamicpricing.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PriceNotFoundExceptionTest {

    private static final String EXPECTED_MESSAGE = "No price found for the given parameters.";
    private static final Throwable CAUSE = new Throwable("Database error");

    @Test
    void shouldCreateExceptionWithMessage() {
        final PriceNotFoundException exception = new PriceNotFoundException(EXPECTED_MESSAGE);

        assertEquals(EXPECTED_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        assertInstanceOf(RuntimeException.class, new PriceNotFoundException(EXPECTED_MESSAGE));
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        final PriceNotFoundException exception = new PriceNotFoundException(EXPECTED_MESSAGE, CAUSE);

        assertEquals(EXPECTED_MESSAGE, exception.getMessage());
        assertEquals(CAUSE, exception.getCause());
    }
}
