package com.taskflow.securetaskflow.common;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response returned by exception handlers.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> details
) {
}
