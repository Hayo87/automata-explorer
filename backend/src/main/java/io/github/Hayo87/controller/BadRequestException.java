package io.github.Hayo87.controller;

/**
 * Thrown to indidact that a client request is invalid and is used 
 * if input validation fails or the required parameters are missing.
 * 
 * Intended to be handeled globally via {@code @ControllerAdvice}.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
