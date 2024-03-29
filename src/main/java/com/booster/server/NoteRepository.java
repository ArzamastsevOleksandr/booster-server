package com.booster.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM note n
            ORDER BY n.last_seen_at
            LIMIT :size
            """)
    List<NoteEntity> findBatch(Integer size);

}
