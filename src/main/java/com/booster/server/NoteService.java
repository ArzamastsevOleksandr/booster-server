package com.booster.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteDto create(CreateNoteInput input) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(input.getContent());
        noteEntity.setLastSeenAt(LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN));

        noteEntity = noteRepository.save(noteEntity);

        return new NoteDto()
                .setContent(noteEntity.getContent());
    }

    public void updateLastSeenAt(UpdateLastSeenAtInput input) {
        List<NoteEntity> updatedNoteEntities = noteRepository.findAllById(input.getIds())
                .stream()
                .map(note -> note.setLastSeenAt(input.getLocalDateTime()))
                .toList();
        noteRepository.saveAll(updatedNoteEntities);
    }

}
