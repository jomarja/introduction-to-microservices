package com.epam.resourceservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(long id) {
        super("Resource with ID=%d not found".formatted(id));
    }
}
