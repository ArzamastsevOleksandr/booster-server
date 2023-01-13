package com.booster.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM vocabulary_entry ve ORDER BY ve.last_seen_at LIMIT :size")
    List<VocabularyEntryEntity> findBatch(Integer size);

}
