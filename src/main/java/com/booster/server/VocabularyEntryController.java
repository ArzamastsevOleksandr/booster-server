package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/vocabulary-entry")
@RequiredArgsConstructor
class VocabularyEntryController {

    private final VocabularyEntryService vocabularyEntryService;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
        // todo: add constraints
    VocabularyEntryDto create(@RequestBody CreateVocabularyEntryInput input) {
        log.debug("Processing request [input={}]", input);
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.create(input);
        log.debug("Added vocabulary entry [dto={}]", vocabularyEntryDto);
        return vocabularyEntryDto;
    }

}
