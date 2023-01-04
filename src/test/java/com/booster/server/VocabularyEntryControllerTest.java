package com.booster.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
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

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    VocabularyEntryRepository vocabularyEntryRepository;
    @Autowired
    VocabularyEntryService vocabularyEntryService;

    @Test
    void createNewVocabularyEntry() throws Exception {
        assertThat(vocabularyEntryRepository.findAll()).isEmpty();

        String name = "coalesce";
        String description = "come together to form one mass or whole";

        mockMvc.perform(MockMvcRequestBuilders.post("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateVocabularyEntryInput()
                                .setName(name)
                                .setDescription(description))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));

        assertThat(vocabularyEntryRepository.findAll())
                .hasSize(1)
                .extracting("name", "description")
                .containsExactly(Tuple.tuple(name, description));
    }

    @Test
    void returnVocabularyEntryListOfPredefinedSize() throws Exception {
        assertThat(vocabularyEntryRepository.findAll()).isEmpty();

        String firstName = "robust";
        String firstDescription = "strong and healthy; hardy; vigorous";
        String secondName = "stuck";
        String secondDescription = "unable to move from a particular position or place, or unable to change a situation";
        Integer expectedSize = 2;

        mockMvc.perform(MockMvcRequestBuilders.post("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateVocabularyEntryInput()
                                .setName(firstName)
                                .setDescription(firstDescription))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(firstName))
                .andExpect(jsonPath("$.description").value(firstDescription));
        mockMvc.perform(MockMvcRequestBuilders.post("/vocabulary-entry")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateVocabularyEntryInput()
                                .setName(secondName)
                                .setDescription(secondDescription))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(secondName))
                .andExpect(jsonPath("$.description").value(secondDescription));

        mockMvc.perform(MockMvcRequestBuilders.get("/vocabulary-entry/list?size=%s".formatted(expectedSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(expectedSize))
                .andExpect((jsonPath("$.list[0].name").value(firstName)))
                .andExpect((jsonPath("$.list[0].description").value(firstDescription)))
                .andExpect((jsonPath("$.list[1].name").value(secondName)))
                .andExpect((jsonPath("$.list[1].description").value(secondDescription)));

        assertThat(vocabularyEntryRepository.findAll())
                .hasSize(expectedSize)
                .extracting("name", "description")
                .containsExactly(Tuple.tuple(firstName, firstDescription), Tuple.tuple(secondName, secondDescription));
    }

}
