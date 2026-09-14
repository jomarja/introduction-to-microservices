package com.epam.songservice.service;

import com.epam.songservice.dto.SongDto;
import com.epam.songservice.dto.SongRequest;
import com.epam.songservice.entity.Song;
import com.epam.songservice.exception.SongMetadataAlreadyExistsException;
import com.epam.songservice.exception.SongMetadataNotFoundException;
import com.epam.songservice.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    private static final SongRequest REQUEST =
            new SongRequest(1L, "Test Song", "Test Artist", "Test Album", "03:45", "2024");

    @Mock
    private SongRepository songRepository;

    @Spy
    private IdParser idParser;

    @InjectMocks
    private SongService songService;

    @Test
    void createStoresMetadataAndReturnsItsId() {
        when(songRepository.existsById(1L)).thenReturn(false);
        when(songRepository.save(any(Song.class))).thenReturn(song());

        assertThat(songService.create(REQUEST)).isEqualTo(1L);
    }

    @Test
    void createFailsWhenMetadataAlreadyExists() {
        when(songRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> songService.create(REQUEST))
                .isInstanceOf(SongMetadataAlreadyExistsException.class)
                .hasMessage("Metadata for resource ID=1 already exists");
    }

    @Test
    void findByIdReturnsStoredMetadata() {
        when(songRepository.findById(1L)).thenReturn(Optional.of(song()));

        assertThat(songService.findById("1"))
                .isEqualTo(new SongDto(1L, "Test Song", "Test Artist", "Test Album", "03:45", "2024"));
    }

    @Test
    void findByIdFailsWhenMetadataDoesNotExist() {
        when(songRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.findById("99999"))
                .isInstanceOf(SongMetadataNotFoundException.class)
                .hasMessage("Song metadata for ID=99999 not found");
    }

    @Test
    void deleteReturnsOnlyExistingIds() {
        when(songRepository.findExistingIds(List.of(1L, 99999L))).thenReturn(List.of(1L));

        assertThat(songService.deleteByIds("1,99999")).containsExactly(1L);
        verify(songRepository).deleteAllByIdInBatch(List.of(1L));
    }

    private Song song() {
        return new Song(REQUEST.id(), REQUEST.name(), REQUEST.artist(), REQUEST.album(),
                REQUEST.duration(), REQUEST.year());
    }
}
