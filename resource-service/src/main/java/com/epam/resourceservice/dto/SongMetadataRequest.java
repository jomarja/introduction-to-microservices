package com.epam.resourceservice.dto;

public record SongMetadataRequest(Long id,
                                  String name,
                                  String artist,
                                  String album,
                                  String duration,
                                  String year) {

    public static SongMetadataRequest from(Long resourceId, Mp3Tags tags) {
        return new SongMetadataRequest(resourceId, tags.name(), tags.artist(), tags.album(),
                tags.duration(), tags.year());
    }
}
