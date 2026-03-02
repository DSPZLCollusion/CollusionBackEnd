package com.collusion.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // 400 — Validation failures (@Valid on @RequestBody)
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiError body = ApiError.ofValidation(request.getRequestURI(), fieldErrors);
        log.warn("Validation failed on {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ----------------------------------------------------------------
    // 400 — Illegal arguments (e.g. duplicate email, bad interest combo)
    // ----------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ApiError body = ApiError.of(400, "Bad Request", ex.getMessage(), request.getRequestURI());
        log.warn("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // ----------------------------------------------------------------
    // 401 — Unauthenticated (invalid or missing JWT)
    // ----------------------------------------------------------------
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        ApiError body = ApiError.of(401, "Unauthorized", "Authentication required", request.getRequestURI());
        log.warn("Authentication failure on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ----------------------------------------------------------------
    // 403 — Authenticated but wrong role (Spring Security)
    // ----------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiError body = ApiError.of(403, "Forbidden", "You do not have permission to perform this action", request.getRequestURI());
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ----------------------------------------------------------------
    // 403 — Authenticated but wrong role (our own exception)
    // ----------------------------------------------------------------
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        ApiError body = ApiError.of(403, "Forbidden", ex.getMessage(), request.getRequestURI());
        log.warn("Unauthorized action '{}' on {}", ex.getAction(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ----------------------------------------------------------------
    // 404 — Resource not found
    // ----------------------------------------------------------------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError body = ApiError.of(404, "Not Found", ex.getMessage(), request.getRequestURI());
        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ----------------------------------------------------------------
    // 500 — Catch-all for anything unexpected
    // ----------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        // Log the full stack trace server-side but never expose internals to the client
        log.error("Unhandled exception on {}: ", request.getRequestURI(), ex);

        ApiError body = ApiError.of(500, "Internal Server Error", "An unexpected error occurred", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}