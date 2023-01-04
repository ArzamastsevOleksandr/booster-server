package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
