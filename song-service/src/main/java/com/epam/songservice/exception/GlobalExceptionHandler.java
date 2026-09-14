package com.epam.songservice.exception;

import com.epam.songservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage,
                        (firstMessage, ignored) -> firstMessage));
        return ResponseEntity.badRequest().body(ErrorResponse.validationError(details));
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidId(InvalidIdException e) {
        return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
        return errorResponse("Song metadata is missing or malformed", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        return errorResponse("Required parameter '%s' is missing".formatted(e.getParameterName()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SongMetadataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SongMetadataNotFoundException e) {
        return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SongMetadataAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(SongMetadataAlreadyExistsException e) {
        return errorResponse(e.getMessage(), HttpStatus.CONFLICT);
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
