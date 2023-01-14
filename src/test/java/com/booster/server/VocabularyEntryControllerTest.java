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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class VocabularyEntryControllerTest {

    static final int ONE_CORRECT_ANSWERS_COUNT = 1;
    static final int DEFAULT_CORRECT_ANSWERS_COUNT = 0;

    final String coalesce = "coalesce";
    final String coalesceDescription = "come together to form one mass or whole";
    final String robust = "robust";
    final String robustDescription = "strong and healthy; hardy; vigorous";
    final String contribution = "contribution";
    final String contributionDescription = "the part played by a person or thing in bringing about a result or helping something to advance";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    VocabularyEntryRepository vocabularyEntryRepository;
    @Autowired
    WordRepository wordRepository;

    @BeforeEach
    void beforeEach() {
        vocabularyEntryRepository.deleteAll();
    }

    @Test
    void createNewVocabularyEntry() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateVocabularyEntryInput()
                                .setName(coalesce)
                                .setDescription(coalesceDescription))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(coalesce))
                .andExpect(jsonPath("$.description").value(coalesceDescription));

        assertThat(vocabularyEntryRepository.findAll())
                .hasSize(1)
                .extracting("word", "description", "correctAnswersCount", "lastSeenAt")
                .containsExactly(Tuple.tuple(
                        wordRepository.findByNameOrCreate(coalesce),
                        coalesceDescription,
                        DEFAULT_CORRECT_ANSWERS_COUNT,
                        LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN))
                );

        assertThat(wordRepository.findByName(coalesce)).isNotEmpty();
    }

    @Test
    void returnVocabularyEntryListOfPredefinedSize() throws Exception {
        Integer expectedSize = 2;

        vocabularyEntryRepository.save(new VocabularyEntryEntity()
                .setWord(wordRepository.findByNameOrCreate(coalesce))
                .setDescription(coalesceDescription)
                .setLastSeenAt(LocalDateTime.now()));
        vocabularyEntryRepository.save(new VocabularyEntryEntity()
                .setWord(wordRepository.findByNameOrCreate(robust))
                .setDescription(robustDescription));

        mockMvc.perform(MockMvcRequestBuilders.get("/vocabulary-entry/list?size=%s".formatted(expectedSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize))
                .andExpect((jsonPath("$.[0].name").value(robust)))
                .andExpect((jsonPath("$.[0].description").value(robustDescription)))
                .andExpect((jsonPath("$.[1].name").value(coalesce)))
                .andExpect((jsonPath("$.[1].description").value(coalesceDescription)));
    }

    @Test
    void correctVocabularyEntryLastSeenAtUpdate() throws Exception {
        Long id1 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(coalesce))
                        .setDescription(coalesceDescription))
                .getId();
        Long id2 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(robust))
                        .setDescription(robustDescription))
                .getId();
        Long id3 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(contribution))
                        .setDescription(contributionDescription))
                .getId();

        LocalDateTime lastSeenAt = LocalDateTime.now();
        List<Long> ids = List.of(id1, id2);
        mockMvc.perform(MockMvcRequestBuilders.patch("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateLastSeenAtInput()
                                .setIds(ids)
                                .setLastSeenAt(lastSeenAt))))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(vocabularyEntryRepository.findAllById(ids))
                .extracting("lastSeenAt")
                .allMatch(lastSeenAt::equals);

        assertThat(vocabularyEntryRepository.findById(id3)
                .map(VocabularyEntryEntity::getLastSeenAt))
                .contains(LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN));
    }

    @Test
    void correctUpdateVocabularyEntryCorrectAnswersCount() throws Exception {
        Long id1 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(coalesce))
                        .setDescription(coalesceDescription))
                .getId();
        Long id2 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(robust))
                        .setDescription(robustDescription))
                .getId();
        Long id3 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(contribution))
                        .setDescription(contributionDescription)
                        .setCorrectAnswersCount(1))
                .getId();

        mockMvc.perform(MockMvcRequestBuilders.patch("/vocabulary-entry/correct-answer-count")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateCorrectAnswersCountInput()
                                .id(id1)
                                .correct(Boolean.TRUE))))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.patch("/vocabulary-entry/correct-answer-count")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateCorrectAnswersCountInput()
                                .id(id2)
                                .correct(Boolean.FALSE))))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.patch("/vocabulary-entry/correct-answer-count")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateCorrectAnswersCountInput()
                                .id(id3)
                                .correct(Boolean.FALSE))))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(ONE_CORRECT_ANSWERS_COUNT, vocabularyEntryRepository.findById(id1).get().getCorrectAnswersCount());
        assertEquals(DEFAULT_CORRECT_ANSWERS_COUNT, vocabularyEntryRepository.findById(id2).get().getCorrectAnswersCount());
        assertEquals(DEFAULT_CORRECT_ANSWERS_COUNT, vocabularyEntryRepository.findById(id3).get().getCorrectAnswersCount());
    }

}
