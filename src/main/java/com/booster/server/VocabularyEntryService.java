package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordRepository wordRepository;

    @Transactional
    public VocabularyEntryDto create(CreateVocabularyEntryInput input) {
        var vocabularyEntryEntity = new VocabularyEntryEntity();

        removeNameFromSynonyms(input);
        WordEntity wordEntity = wordRepository.findByNameOrCreate(input.getName());
        vocabularyEntryEntity.setWord(wordEntity);
        vocabularyEntryEntity.setDescription(input.getDescription());
        vocabularyEntryEntity.setSynonyms(synonyms(input));

        return vocabularyEntryDto(vocabularyEntryRepository.save(vocabularyEntryEntity));
    }

    private void removeNameFromSynonyms(CreateVocabularyEntryInput input) {
        if (input.getSynonyms().contains(input.getName())) {
            log.info("Removing the duplicate word from synonyms [input={}]", input);
            input.getSynonyms().remove(input.getName());
        }
    }

    private Set<WordEntity> synonyms(CreateVocabularyEntryInput input) {
        return input.getSynonyms().stream().map(wordRepository::findByNameOrCreate).collect(toSet());
    }

    public List<VocabularyEntryDto> batchOfSize(Integer size) {
        return vocabularyEntryRepository.findBatch(size)
                .stream()
                .map(this::vocabularyEntryDto)
                .toList();
    }

    private VocabularyEntryDto vocabularyEntryDto(VocabularyEntryEntity entity) {
        return new VocabularyEntryDto()
                .setId(entity.getId())
                .setName(entity.getWord().getName())
                .setDescription(entity.getDescription())
                // todo: analyze the queries on Lazy fields
                .setSynonyms(entity.getSynonyms().stream().map(WordEntity::getName).collect(toSet()));
    }

    @Transactional
    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<VocabularyEntryEntity> vocabularyEntries = vocabularyEntryRepository.findAllById(input.getIds());
        vocabularyEntries.forEach(entry -> entry.setLastSeenAt(input.getLastSeenAt()));
        // todo: check if saveAll leads to the N+1 problem
        vocabularyEntryRepository.saveAll(vocabularyEntries);
    }

    @Transactional
    public void updateCorrectAnswersCount(UpdateCorrectAnswersCountInput input) {
        Long id = input.id();
        VocabularyEntryEntity vocabularyEntry = vocabularyEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary entry not found [id=%s]".formatted(id)));
        Integer oldCount = vocabularyEntry.getCorrectAnswersCount();
        if (input.correct()) {
            vocabularyEntry.setCorrectAnswersCount(++oldCount);
        } else {
            vocabularyEntry.setCorrectAnswersCount(oldCount == 0 ? 0 : --oldCount);
        }
        vocabularyEntryRepository.save(vocabularyEntry);
    }

}
