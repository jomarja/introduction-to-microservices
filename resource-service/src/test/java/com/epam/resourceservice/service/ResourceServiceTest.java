package com.epam.resourceservice.service;

import com.epam.resourceservice.client.SongServiceClient;
import com.epam.resourceservice.dto.Mp3Tags;
import com.epam.resourceservice.entity.AudioResource;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    private static final byte[] AUDIO_DATA = {1, 2, 3};
    private static final Mp3Tags TAGS = new Mp3Tags("Test Title", "Test Artist", "Test Album", "00:07", "2025");

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private Mp3TagExtractor mp3TagExtractor;

    @Mock
    private SongServiceClient songServiceClient;

    @Spy
    private IdParser idParser;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void uploadStoresResourceAndSendsTagsToSongService() {
        when(mp3TagExtractor.extract(AUDIO_DATA)).thenReturn(TAGS);
        when(resourceRepository.save(any(AudioResource.class))).thenReturn(savedResource(1L));

        assertThat(resourceService.upload(AUDIO_DATA)).isEqualTo(1L);
        verify(songServiceClient).createSongMetadata(1L, TAGS);
    }

    @Test
    void downloadReturnsStoredAudioData() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(savedResource(1L)));

        assertThat(resourceService.download("1")).isEqualTo(AUDIO_DATA);
    }

    @Test
    void downloadFailsWhenResourceDoesNotExist() {
        when(resourceRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.download("99999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with ID=99999 not found");
    }

    @Test
    void deleteReturnsOnlyExistingIdsAndRemovesTheirMetadata() {
        when(resourceRepository.findExistingIds(List.of(1L, 101L, 102L))).thenReturn(List.of(1L));

        assertThat(resourceService.deleteByIds("1,101,102")).containsExactly(1L);
        verify(resourceRepository).deleteAllByIdInBatch(List.of(1L));
        verify(songServiceClient).deleteSongMetadata(List.of(1L));
    }

    @Test
    void deleteSkipsSongServiceWhenNothingWasDeleted() {
        when(resourceRepository.findExistingIds(List.of(99999L))).thenReturn(List.of());

        assertThat(resourceService.deleteByIds("99999")).isEmpty();
        verify(songServiceClient, never()).deleteSongMetadata(any());
    }

    private AudioResource savedResource(Long id) {
        AudioResource resource = new AudioResource(AUDIO_DATA);
        ReflectionTestUtils.setField(resource, "id", id);
        return resource;
    }
}
