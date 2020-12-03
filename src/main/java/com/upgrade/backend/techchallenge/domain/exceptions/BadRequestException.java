package com.upgrade.backend.techchallenge.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * HTTP 400
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
