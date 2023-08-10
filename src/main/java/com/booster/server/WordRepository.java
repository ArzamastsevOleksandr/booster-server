package com.booster.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface WordRepository extends JpaRepository<WordEntity, Long> {

    Optional<WordEntity> findByName(String name);

    // todo: on conflict do return
    @Transactional
    default WordEntity findByNameOrCreate(String name) {
        return findByName(name).orElseGet(() -> save(new WordEntity().setName(name)));
    }

}
