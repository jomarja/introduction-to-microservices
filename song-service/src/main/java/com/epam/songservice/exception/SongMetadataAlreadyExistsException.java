package com.epam.songservice.exception;

public class SongMetadataAlreadyExistsException extends RuntimeException {

    public SongMetadataAlreadyExistsException(long resourceId) {
        super("Metadata for resource ID=%d already exists".formatted(resourceId));
    }
}
