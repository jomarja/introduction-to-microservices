package com.epam.resourceservice.service;

import com.epam.resourceservice.exception.InvalidMp3Exception;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mp3TagExtractorTest {

    private final Mp3TagExtractor mp3TagExtractor = new Mp3TagExtractor();

    @Test
    void rejectsAudioDataThatIsNotMp3() {
        byte[] notAnMp3 = "definitely not an mp3".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> mp3TagExtractor.extract(notAnMp3))
                .isInstanceOf(InvalidMp3Exception.class)
                .hasMessageContaining("Only MP3 files are allowed");
    }
}
