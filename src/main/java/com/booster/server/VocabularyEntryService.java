package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordRepository wordRepository;

    @Transactional
    public VocabularyEntryDto create(CreateVocabularyEntryInput input) {
        WordEntity wordEntity = wordRepository.findByNameOrCreate(input.getName());

        var vocabularyEntryEntity = new VocabularyEntryEntity();
        vocabularyEntryEntity.setWord(wordEntity);
        vocabularyEntryEntity.setDescription(input.getDescription());

        return vocabularyEntryDto(vocabularyEntryRepository.save(vocabularyEntryEntity));
    }

    public List<VocabularyEntryDto> batchOfSize(Integer size) {
        return vocabularyEntryRepository.findBatch(size)
                .stream()
                .map(this::vocabularyEntryDto)
                .toList();
    }

    private VocabularyEntryDto vocabularyEntryDto(VocabularyEntryEntity entity) {
        return new VocabularyEntryDto()
                .setName(entity.getWord().getName())
                .setDescription(entity.getDescription());
    }

    @Transactional
    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<VocabularyEntryEntity> vocabularyEntries = vocabularyEntryRepository.findAllById(input.getIds());
        vocabularyEntries.forEach(entry -> entry.setLastSeenAt(input.getLastSeenAt()));
        vocabularyEntryRepository.saveAll(vocabularyEntries);
    }

    public void updateCorrectAnswersCount(UpdateCorrectAnswersCount input) {
        Long id = input.getId();
        VocabularyEntryEntity vocabularyEntry = vocabularyEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary entry with id=%s not found".formatted(id)));
        Integer oldCount = vocabularyEntry.getCorrectAnswersCount();
        if (input.getIsCorrect()) {
            vocabularyEntry.setCorrectAnswersCount(++oldCount);
        } else {
            vocabularyEntry.setCorrectAnswersCount(oldCount == 0 ? 0 : --oldCount);
        }
        vocabularyEntryRepository.save(vocabularyEntry);
    }

}
