package com.epam.songservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Song metadata. The identifier is the ID of the resource the metadata belongs to.
 */
@Entity
@Table(name = "songs")
public class Song {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String album;

    @Column(nullable = false)
    private String duration;

    @Column(name = "release_year", nullable = false)
    private String year;

    protected Song() {
    }

    public Song(Long id, String name, String artist, String album, String duration, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getYear() {
        return year;
    }
}
