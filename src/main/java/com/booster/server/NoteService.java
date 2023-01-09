package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteDto create(CreateNoteInput input) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(input.getContent());

        noteEntity = noteRepository.save(noteEntity);

        return new NoteDto()
                .setContent(noteEntity.getContent());
    }

    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<NoteEntity> notes = noteRepository.findAllById(input.getIds());
        notes.forEach(note -> note.setLastSeenAt(input.getLastSeenAt()));
        noteRepository.saveAll(notes);
    }

}
