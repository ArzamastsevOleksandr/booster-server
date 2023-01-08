package com.booster.server;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NoteControllerTest {

    final String firstNote = "My first note";
    final String secondNote = "Read 10 pages";
    final String thirdNote = "Practice piano";

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
        assertThat(noteRepository.findAll()).isEmpty();

        mockMvc.perform(MockMvcRequestBuilders.post("/note")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateNoteInput().setContent(firstNote))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(firstNote));

        assertThat(noteRepository.findAll())
                .hasSize(1)
                .extracting("content")
                .containsExactly(firstNote);
    }

    @Test
    void correctUpdateOfLastSeenAtPropertyOfVocabularyEntry() throws Exception {
        assertThat(noteRepository.findAll()).isEmpty();

        LocalDateTime initDateTime = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN);
        Long id1 = noteRepository.save(new NoteEntity()
                        .setContent(firstNote)
                        .setLastSeenAt(initDateTime))
                .getId();
        noteRepository.save(new NoteEntity()
                .setContent(secondNote)
                .setLastSeenAt(initDateTime));
        Long id3 = noteRepository.save(new NoteEntity()
                        .setContent(thirdNote)
                        .setLastSeenAt(initDateTime))
                .getId();

        LocalDateTime updateDateTime = LocalDateTime.now();
        List<Long> ids = List.of(id1, id3);
        mockMvc.perform(MockMvcRequestBuilders.patch("/note")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateLastSeenAtInput()
                                .setIds(ids)
                                .setLocalDateTime(updateDateTime))))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(noteRepository.findAllById(ids)
                .stream()
                .map(NoteEntity::getLastSeenAt)
                .toList())
                .containsOnly(updateDateTime);
    }

}
