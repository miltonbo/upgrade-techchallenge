package com.upgrade.backend.techchallenge.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * HTTP 409
 */
public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
