package io.github.Hayo87.dto;

/**
 * Represents a uniform error response
 * 
 * @param message the error message
 * @param status the HTTP status code
 */
public record ErrorDTO( String message, int status) {} 