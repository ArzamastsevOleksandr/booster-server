package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;

    @Transactional
    public NoteDto create(CreateNoteInput input) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(input.getContent());

        return noteDto(noteRepository.save(noteEntity));
    }

    private NoteDto noteDto(NoteEntity noteEntity) {
        return new NoteDto()
                .setId(noteEntity.getId())
                .setContent(noteEntity.getContent());
    }

    @Transactional
    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<NoteEntity> notes = noteRepository.findAllById(input.getIds());
        notes.forEach(note -> note.setLastSeenAt(input.getLastSeenAt()));
        noteRepository.saveAll(notes);
    }

    // todo: research returning a stream from the DB and how performant it is
    public List<NoteDto> batchOfSize(Integer size) {
        return noteRepository.findBatch(size)
                .stream()
                .map(this::noteDto)
                .toList();
    }

}
