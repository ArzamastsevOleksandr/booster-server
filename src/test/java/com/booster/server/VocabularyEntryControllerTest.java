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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
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
    final String unite = "unite";
    final String combine = "combine";
    final Set<String> coalesceSynonyms = Set.of(coalesce, unite, combine);
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
    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionTemplate transactionTemplate;

    @BeforeEach
    void beforeEach() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        vocabularyEntryRepository.deleteAll();
    }

    @Test
    void createNewVocabularyEntry() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateVocabularyEntryInput()
                                .setName(coalesce)
                                .setDescription(coalesceDescription)
                                .setSynonyms(coalesceSynonyms))))
                .andDo(print())
                .andExpect(status().isCreated())
                // todo: write custom captors to save the id from the json response
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(coalesce))
                .andExpect(jsonPath("$.description").value(coalesceDescription))
                // todo: write a custom Matcher implementation to perform assertions on Collection
                .andExpect(jsonPath("$.synonyms", hasItem(unite)))
                .andExpect(jsonPath("$.synonyms", hasItem(combine)))
                .andExpect(jsonPath("$.synonyms", not(hasItem(coalesce))));

        // todo: query by id directly and perform explicit checks like assertThat(entity.getWord()).isEqualTo(...)
        transactionTemplate.execute(status -> {
            assertThat(vocabularyEntryRepository.findAll())
                    .hasSize(1)
                    // the downside of "extracting" is the hardcoded property names. The tests fill require a manual update if the property names change
                    .extracting("word", "description", "correctAnswersCount", "lastSeenAt", "synonyms")
                    .containsExactly(Tuple.tuple(
                            wordRepository.findByNameOrCreate(coalesce),
                            coalesceDescription,
                            DEFAULT_CORRECT_ANSWERS_COUNT,
                            LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN),
                            Stream.of(combine, unite).map(wordRepository::findByNameOrCreate).collect(toSet()))
                    );
            return status;
        });
        assertThat(wordRepository.findByName(coalesce)).isNotEmpty();
        assertThat(wordRepository.findByName(combine)).isNotEmpty();
        assertThat(wordRepository.findByName(unite)).isNotEmpty();
    }

    @Test
    void returnsBatchOfVocabularyEntriesWithRespectToLastSeenAt() throws Exception {
        Long id1 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(coalesce))
                        .setDescription(coalesceDescription)
                        .setLastSeenAt(LocalDateTime.now())
                        .setSynonyms(coalesceSynonyms.stream().map(wordRepository::findByNameOrCreate).collect(toSet())))
                .getId();
        Long id2 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(robust))
                        .setDescription(robustDescription))
                .getId();

        Integer batchSize = 2;

        mockMvc.perform(MockMvcRequestBuilders.get("/vocabulary-entry/list?size=%s".formatted(batchSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(batchSize))
                .andExpect(jsonPath("$.[0].id").value(id2))
                .andExpect(jsonPath("$.[0].name").value(robust))
                .andExpect(jsonPath("$.[0].description").value(robustDescription))
                .andExpect(jsonPath("$.[1].id").value(id1))
                .andExpect(jsonPath("$.[1].name").value(coalesce))
                .andExpect(jsonPath("$.[1].description").value(coalesceDescription))
                .andExpect(jsonPath("$.[1].synonyms", hasItem(combine)))
                .andExpect(jsonPath("$.[1].synonyms", hasItem(unite)));
    }

    @Test
    void correctVocabularyEntryLastSeenAtUpdate() throws Exception {
        Long id1 = vocabularyEntryRepository.save(new VocabularyEntryEntity()
                        .setWord(wordRepository.findByNameOrCreate(coalesce))
                        .setDescription(coalesceDescription)
                        .setSynonyms(coalesceSynonyms.stream().map(wordRepository::findByNameOrCreate).collect(toSet())))
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
                        .setDescription(coalesceDescription)
                        .setSynonyms(coalesceSynonyms.stream().map(wordRepository::findByNameOrCreate).collect(toSet())))
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
