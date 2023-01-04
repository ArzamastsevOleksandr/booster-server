package com.booster.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM vocabulary_entry ve LIMIT :size")
    List<VocabularyEntryEntity> findAllWithLimit(Integer size);

}
