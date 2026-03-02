package com.collusion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    private final String action;

    public UnauthorizedException(String action) {
        super(String.format("Access denied: insufficient permissions to perform '%s'", action));
        this.action = action;
    }

    public UnauthorizedException(String action, String reason) {
        super(String.format("Access denied: cannot perform '%s' — %s", action, reason));
        this.action = action;
    }

    public String getAction() { return action; }
}