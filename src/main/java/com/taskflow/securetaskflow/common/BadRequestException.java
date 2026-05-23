package com.taskflow.securetaskflow.common;

/**
 * Exception used when a request violates business validation rules.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
