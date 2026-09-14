package com.epam.resourceservice.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String errorMessage, String errorCode) {

    public static ErrorResponse of(String errorMessage, HttpStatus status) {
        return new ErrorResponse(errorMessage, String.valueOf(status.value()));
    }
}
