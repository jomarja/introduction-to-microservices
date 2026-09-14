package com.epam.resourceservice.exception;

import com.epam.resourceservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({InvalidIdException.class, InvalidMp3Exception.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e) {
        return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedContentType(HttpMediaTypeNotSupportedException e) {
        String contentType = e.getContentType() == null ? "unknown" : e.getContentType().toString();
        return errorResponse("Invalid file format: %s. Only MP3 files are allowed".formatted(contentType),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
        return errorResponse("Invalid MP3 file: the request body is missing or unreadable", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        return errorResponse("Required parameter '%s' is missing".formatted(e.getParameterName()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("Unexpected error while processing the request", e);
        return errorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ErrorResponse.of(message, status));
    }
}
