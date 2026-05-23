package com.taskflow.securetaskflow.common;

/**
 * Exception used when an authenticated user lacks required access.
 */
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
