package com.booster.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

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

}
