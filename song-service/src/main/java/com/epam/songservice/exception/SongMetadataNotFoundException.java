package com.epam.songservice.exception;

public class SongMetadataNotFoundException extends RuntimeException {

    public SongMetadataNotFoundException(long id) {
        super("Song metadata for ID=%d not found".formatted(id));
    }
}
