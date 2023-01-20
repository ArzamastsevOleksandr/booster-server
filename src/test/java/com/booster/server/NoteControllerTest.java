package com.booster.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NoteControllerTest {

    final String firstNoteContent = "My first note";
    final String secondNoteContent = "Read 10 pages";
    final String thirdNoteContent = "Practice piano";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    NoteRepository noteRepository;

    @BeforeEach
    void beforeEach() {
        noteRepository.deleteAll();
    }

    @Test
    void createNewNote() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/note")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateNoteInput().setContent(firstNoteContent))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(firstNoteContent));

        assertThat(noteRepository.findAll())
                .hasSize(1)
                .extracting("content", "lastSeenAt")
                .containsExactly(Tuple.tuple(firstNoteContent, LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN)));
    }

    @Test
    void correctNoteLastSeenAtUpdate() throws Exception {
        Long id1 = noteRepository.save(new NoteEntity()
                        .setContent(firstNoteContent))
                .getId();
        Long id2 = noteRepository.save(new NoteEntity()
                        .setContent(secondNoteContent))
                .getId();
        Long id3 = noteRepository.save(new NoteEntity()
                        .setContent(thirdNoteContent))
                .getId();

        LocalDateTime lastSeenAt = LocalDateTime.now();
        List<Long> ids = List.of(id1, id3);
        mockMvc.perform(MockMvcRequestBuilders.patch("/note")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateLastSeenAtInput()
                                .setIds(ids)
                                .setLastSeenAt(lastSeenAt))))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(noteRepository.findAllById(ids)
                .stream()
                .allMatch(n -> n.getLastSeenAt().equals(lastSeenAt)));
        assertThat(noteRepository.findById(id2)
                .map(NoteEntity::getLastSeenAt))
                .contains(LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN));
    }

    @Test
    void returnsBatchOfNotesWithRespectToLastSeenAt() throws Exception {
        Long id1 = noteRepository.save(new NoteEntity()
                        .setContent(firstNoteContent)
                        .setLastSeenAt(LocalDateTime.now().minusHours(1)))
                .getId();
        Long id2 = noteRepository.save(new NoteEntity()
                        .setContent(secondNoteContent))
                .getId();
        noteRepository.save(new NoteEntity()
                .setContent(thirdNoteContent)
                .setLastSeenAt(LocalDateTime.now()));

        Integer batchSize = 2;

        mockMvc.perform(MockMvcRequestBuilders.get("/note/list?size=%s".formatted(batchSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(batchSize))
                .andExpect(jsonPath("$.[0].id").value(id2))
                .andExpect(jsonPath("$.[0].content").value(secondNoteContent))
                .andExpect(jsonPath("$.[1].id").value(id1))
                .andExpect(jsonPath("$.[1].content").value(firstNoteContent));
    }

}
