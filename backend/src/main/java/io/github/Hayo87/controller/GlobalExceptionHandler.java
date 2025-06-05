package io.github.Hayo87.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.Hayo87.dto.ErrorDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles JSON parse errors such a malformed input or invalid enum values. Triggers when 
     * the request can not be read to the DTO.
     * 
     * @param e the exception during deserialization
     * @return a 400 bad request response with error information
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleBadRequest(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorDTO("Invalid request: " + e.getMostSpecificCause().getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Handles the (@link BadRequestException) thrown in the application. 
     * @param e the exception during processing
     * @return a 400 bad request response with error information
     */
    @ExceptionHandler( BadRequestException.class)
    public ResponseEntity<ErrorDTO> handleBadRequest(BadRequestException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorDTO("Invalid request: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    /**
    * Handles validation failures for request DTOs annotated with {@code @Valid}
    *
    * @param e the exception with validation details
    * @return a 400 bad request response with error information
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidation(MethodArgumentNotValidException e) {
        String message = "Input validation failed";

        if(!e.getBindingResult().getFieldErrors().isEmpty()) {
            message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }

        return ResponseEntity.badRequest()
            .body(new ErrorDTO(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError()
            .body(new ErrorDTO("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
