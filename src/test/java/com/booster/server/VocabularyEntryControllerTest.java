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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class VocabularyEntryControllerTest {

    final String coalesce = "coalesce";
    final String coalesceDescription = "come together to form one mass or whole";
    final String robust = "robust";
    final String robustDescription = "strong and healthy; hardy; vigorous";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    VocabularyEntryRepository vocabularyEntryRepository;

    @BeforeEach
    void beforeEach() {
        vocabularyEntryRepository.deleteAll();
    }

    @Test
    void createNewVocabularyEntry() throws Exception {
        assertThat(vocabularyEntryRepository.findAll()).isEmpty();

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
                .extracting("name", "description")
                .containsExactly(Tuple.tuple(coalesce, coalesceDescription));
    }

    @Test
    void returnVocabularyEntryListOfPredefinedSize() throws Exception {
        assertThat(vocabularyEntryRepository.findAll()).isEmpty();

        Integer expectedSize = 2;

        vocabularyEntryRepository.save(new VocabularyEntryEntity()
                .setName(coalesce)
                .setDescription(coalesceDescription));
        vocabularyEntryRepository.save(new VocabularyEntryEntity()
                .setName(robust)
                .setDescription(robustDescription));

        mockMvc.perform(MockMvcRequestBuilders.get("/vocabulary-entry/list?size=%s".formatted(expectedSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize))
                .andExpect((jsonPath("$.[0].name").value(coalesce)))
                .andExpect((jsonPath("$.[0].description").value(coalesceDescription)))
                .andExpect((jsonPath("$.[1].name").value(robust)))
                .andExpect((jsonPath("$.[1].description").value(robustDescription)));


        assertThat(vocabularyEntryRepository.findAll())
                .hasSize(expectedSize)
                .extracting("name", "description")
                .containsExactly(Tuple.tuple(coalesce, coalesceDescription), Tuple.tuple(robust, robustDescription));
    }

}
