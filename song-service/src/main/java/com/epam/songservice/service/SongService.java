package com.epam.songservice.service;

import com.epam.songservice.dto.SongDto;
import com.epam.songservice.dto.SongRequest;
import com.epam.songservice.exception.SongMetadataAlreadyExistsException;
import com.epam.songservice.exception.SongMetadataNotFoundException;
import com.epam.songservice.mapper.SongMapper;
import com.epam.songservice.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SongService {

    private final SongRepository songRepository;
    private final IdParser idParser;

    public SongService(SongRepository songRepository, IdParser idParser) {
        this.songRepository = songRepository;
        this.idParser = idParser;
    }

    public Long create(SongRequest request) {
        if (songRepository.existsById(request.id())) {
            throw new SongMetadataAlreadyExistsException(request.id());
        }
        return songRepository.save(SongMapper.toEntity(request)).getId();
    }

    @Transactional(readOnly = true)
    public SongDto findById(String rawId) {
        long id = idParser.parseId(rawId);
        return songRepository.findById(id)
                .map(SongMapper::toDto)
                .orElseThrow(() -> new SongMetadataNotFoundException(id));
    }

    public List<Long> deleteByIds(String csvIds) {
        List<Long> existingIds = songRepository.findExistingIds(idParser.parseCsv(csvIds));
        songRepository.deleteAllByIdInBatch(existingIds);
        return existingIds;
    }
}
