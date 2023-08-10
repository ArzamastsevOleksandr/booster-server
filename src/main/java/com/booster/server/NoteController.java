package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
class NoteController {

    private final NoteService noteService;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
        // todo: add validations
    NoteDto create(@RequestBody CreateNoteInput input) {
        log.debug("Processing request [input={}]", input);
        NoteDto noteDto = noteService.create(input);
        log.debug("Added note [dto={}]", noteDto);
        return noteDto;
    }

    @PatchMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
        // todo: add constraints
    void updateLastSeenAt(@RequestBody UpdateLastSeenAtInput input) {
        log.debug("Processing request [input={}]", input);
        noteService.updateLastSeenAt(input);
        log.debug("Request processed [input={}]", input);
    }

    @GetMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
        // todo: add constraints
    List<NoteDto> list(@RequestParam Integer size) {
        return noteService.batchOfSize(size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable Long id) {
        noteService.deleteById(id);
    }

}
