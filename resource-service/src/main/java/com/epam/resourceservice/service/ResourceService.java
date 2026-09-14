package com.epam.resourceservice.service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.dto.Mp3Tags;
import com.epam.resourceservice.entity.AudioResource;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3TagExtractor mp3TagExtractor;
    private final SongServiceClient songServiceClient;
    private final IdParser idParser;

    public ResourceService(ResourceRepository resourceRepository,
                           Mp3TagExtractor mp3TagExtractor,
                           SongServiceClient songServiceClient,
                           IdParser idParser) {
        this.resourceRepository = resourceRepository;
        this.mp3TagExtractor = mp3TagExtractor;
        this.songServiceClient = songServiceClient;
        this.idParser = idParser;
    }

    public Long upload(byte[] audioData) {
        Mp3Tags tags = mp3TagExtractor.extract(audioData);
        Long resourceId = resourceRepository.save(new AudioResource(audioData)).getId();
        songServiceClient.createSongMetadata(resourceId, tags);
        return resourceId;
    }

    @Transactional(readOnly = true)
    public byte[] download(String rawId) {
        long id = idParser.parseId(rawId);
        return resourceRepository.findById(id)
                .map(AudioResource::getData)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<Long> deleteByIds(String csvIds) {
        List<Long> existingIds = resourceRepository.findExistingIds(idParser.parseCsv(csvIds));
        if (existingIds.isEmpty()) {
            return List.of();
        }
        resourceRepository.deleteAllByIdInBatch(existingIds);
        songServiceClient.deleteSongMetadata(existingIds);
        return existingIds;
    }
}
