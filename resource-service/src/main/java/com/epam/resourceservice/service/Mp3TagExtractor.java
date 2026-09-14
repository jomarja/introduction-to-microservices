package com.epam.resourceservice.service;

import com.epam.resourceservice.Mp3;
import com.epam.resourceservice.dto.Mp3Tags;
import com.epam.resourceservice.exception.InvalidMp3Exception;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts song tags from MP3 audio data. The tags are passed to the Song Service as they are,
 * only the duration is converted from seconds to the mm:ss format.
 */
@Component
public class Mp3TagExtractor {

    private final Tika tika = new Tika();

    public Mp3Tags extract(byte[] audioData) {
        verifyMp3(audioData);
        Metadata metadata = parse(audioData);
        return new Mp3Tags(
                metadata.get(TikaCoreProperties.TITLE),
                metadata.get(XMPDM.ARTIST),
                metadata.get(XMPDM.ALBUM),
                Mp3Duration.toMinutesAndSeconds(metadata.get(XMPDM.DURATION)),
                metadata.get(XMPDM.RELEASE_DATE));
    }

    private void verifyMp3(byte[] audioData) {
        String detectedType = tika.detect(audioData);
        if (!Mp3.MIME_TYPE.equals(detectedType)) {
            throw new InvalidMp3Exception("Invalid file format: %s. Only MP3 files are allowed".formatted(detectedType));
        }
    }

    private Metadata parse(byte[] audioData) {
        Metadata metadata = new Metadata();
        try (InputStream audioStream = new ByteArrayInputStream(audioData)) {
            new Mp3Parser().parse(audioStream, new DefaultHandler(), metadata, new ParseContext());
        } catch (IOException | SAXException | TikaException e) {
            throw new InvalidMp3Exception("Invalid MP3 file: unable to read the audio data");
        }
        return metadata;
    }
}
