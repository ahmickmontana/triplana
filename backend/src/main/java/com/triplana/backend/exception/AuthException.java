package com.triplana.backend.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String field;

    public AuthException(String field, String message) {
        super(message);
        this.field = field;
    }

    public AuthException(String message) {
        super(message);
        this.field = null;
    }
}
