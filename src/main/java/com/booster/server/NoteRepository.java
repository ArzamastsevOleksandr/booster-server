package com.booster.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM note n ORDER BY n.last_seen_at ASC LIMIT :size")
    List<NoteEntity> findAllWithLimit(Integer size);

}
