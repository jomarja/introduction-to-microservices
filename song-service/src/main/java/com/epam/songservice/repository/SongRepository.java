package com.epam.songservice.repository;

import com.epam.songservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("select s.id from Song s where s.id in :ids")
    List<Long> findExistingIds(@Param("ids") Collection<Long> ids);
}
