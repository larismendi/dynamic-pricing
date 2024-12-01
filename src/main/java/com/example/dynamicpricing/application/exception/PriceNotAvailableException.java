package com.example.dynamicpricing.application.exception;

public class PriceNotAvailableException extends RuntimeException {
    public PriceNotAvailableException(String message) {
        super(message);
    }
}
