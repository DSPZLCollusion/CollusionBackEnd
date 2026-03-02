package com.collusion.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Standardized error response body returned by GlobalExceptionHandler.
 * All API errors share this shape so the React Native client
 * can handle them uniformly.
 *
 * Example JSON:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Member not found with id: 42",
 *   "path": "/api/members/42",
 *   "timestamp": "2025-03-01T14:32:00Z",
 *   "fieldErrors": null
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<FieldError> fieldErrors   // only present on validation failures
) {
    public record FieldError(String field, String message) {}

    /** Convenience factory for simple errors */
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, Instant.now(), null);
    }

    /** Convenience factory for validation errors */
    public static ApiError ofValidation(String path, List<FieldError> fieldErrors) {
        return new ApiError(400, "Bad Request", "Validation failed", path, Instant.now(), fieldErrors);
    }
}