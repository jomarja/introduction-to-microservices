package com.epam.resourceservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
public class AudioResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data", nullable = false)
    private byte[] data;

    protected AudioResource() {
    }

    public AudioResource(byte[] data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
