package com.collusion.api.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Invoked by Spring Security when an unauthenticated request hits a
 * protected resource and an AuthenticationException is thrown.
 *
 * In a stateless REST API we never redirect to a login page.  Instead
 * we return a structured JSON 401 response so clients can handle it
 * programmatically.
 *
 * The response body matches the shape of Spring Boot's default
 * /error response so clients only need one error-handling code path.
 */
@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Unauthorized request to {}: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // LinkedHashMap preserves insertion order in the JSON output
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error",     "Unauthorized");
        body.put("message",   authException.getMessage());
        body.put("path",      request.getServletPath());
        body.put("timestamp", Instant.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}