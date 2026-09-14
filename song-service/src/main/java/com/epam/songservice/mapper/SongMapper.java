package com.epam.songservice.mapper;

import com.epam.songservice.dto.SongDto;
import com.epam.songservice.dto.SongRequest;
import com.epam.songservice.entity.Song;

public final class SongMapper {

    private SongMapper() {
    }

    public static Song toEntity(SongRequest request) {
        return new Song(request.id(), request.name(), request.artist(), request.album(),
                request.duration(), request.year());
    }

    public static SongDto toDto(Song song) {
        return new SongDto(song.getId(), song.getName(), song.getArtist(), song.getAlbum(),
                song.getDuration(), song.getYear());
    }
}
