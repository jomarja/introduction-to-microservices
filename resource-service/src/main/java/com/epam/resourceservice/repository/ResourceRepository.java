package com.epam.resourceservice.repository;

import com.epam.resourceservice.entity.AudioResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ResourceRepository extends JpaRepository<AudioResource, Long> {

    @Query("select r.id from AudioResource r where r.id in :ids")
    List<Long> findExistingIds(@Param("ids") Collection<Long> ids);
}
