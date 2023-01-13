package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
        // todo: add constraints
    List<VocabularyEntryDto> list(@RequestParam Integer size) {
        return vocabularyEntryService.batchOfSize(size);
    }

    @PatchMapping(produces = APPLICATION_JSON_VALUE)
        // todo: add constraints
    void updateLastSeenAt(@RequestBody UpdateLastSeenAtInput input) {
        log.debug("Processing request [input={}]", input);
        vocabularyEntryService.updateLastSeenAt(input);
        log.debug("Request processed [input={}]", input);
    }

}
