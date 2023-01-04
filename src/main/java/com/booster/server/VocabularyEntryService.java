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

        return getVocabularyEntryDto(vocabularyEntryEntity);
    }

    public VocabularyEntryListDto list(Integer size) {
        List<VocabularyEntryDto> list = vocabularyEntryRepository.findAll()
                .stream()
                .limit(size)
                .map(this::getVocabularyEntryDto)
                .collect(Collectors.toList());

        return new VocabularyEntryListDto()
                .setList(list);
    }

    private VocabularyEntryDto getVocabularyEntryDto(VocabularyEntryEntity entity) {
        return new VocabularyEntryDto()
                .setName(entity.getName())
                .setDescription(entity.getDescription());
    }

}
