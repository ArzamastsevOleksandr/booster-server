package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;

    public VocabularyEntryDto create(CreateVocabularyEntryInput input) {
        var vocabularyEntryEntity = new VocabularyEntryEntity();
        vocabularyEntryEntity.setName(input.getName());
        vocabularyEntryEntity.setDescription(input.getDescription());

        vocabularyEntryEntity = vocabularyEntryRepository.save(vocabularyEntryEntity);

        return new VocabularyEntryDto()
                .setName(vocabularyEntryEntity.getName())
                .setDescription(vocabularyEntryEntity.getDescription());
    }

}
