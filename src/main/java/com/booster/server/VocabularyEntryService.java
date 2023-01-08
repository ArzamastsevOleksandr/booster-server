package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;

    public VocabularyEntryDto create(CreateVocabularyEntryInput input) {
        var vocabularyEntryEntity = new VocabularyEntryEntity();
        vocabularyEntryEntity.setName(input.getName());
        vocabularyEntryEntity.setDescription(input.getDescription());
        vocabularyEntryEntity.setLastSeenAt(LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN));

        vocabularyEntryEntity = vocabularyEntryRepository.save(vocabularyEntryEntity);

        return vocabularyEntryDto(vocabularyEntryEntity);
    }

    public List<VocabularyEntryDto> list(Integer size) {
        return vocabularyEntryRepository.findAllWithLimit(size)
                .stream()
                .map(this::vocabularyEntryDto)
                .collect(Collectors.toList());
    }

    private VocabularyEntryDto vocabularyEntryDto(VocabularyEntryEntity entity) {
        return new VocabularyEntryDto()
                .setName(entity.getName())
                .setDescription(entity.getDescription());
    }

    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<VocabularyEntryEntity> updatedVocabularyEntries = vocabularyEntryRepository.findAllById(input.getIds())
                .stream()
                .peek(entry -> entry.setLastSeenAt(input.getLocalDateTime()))
                .toList();
        vocabularyEntryRepository.saveAll(updatedVocabularyEntries);
    }

}
