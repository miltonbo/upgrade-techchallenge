package com.upgrade.backend.techchallenge.domain.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends Exception {
    private HttpStatus status;
    private String error;

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.error = message;
    }
}
