package com.epam.songservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String errorMessage, Map<String, String> details, String errorCode) {

    public static ErrorResponse of(String errorMessage, HttpStatus status) {
        return new ErrorResponse(errorMessage, null, String.valueOf(status.value()));
    }

    public static ErrorResponse validationError(Map<String, String> details) {
        return new ErrorResponse("Validation error", details, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}
