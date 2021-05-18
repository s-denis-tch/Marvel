package com.marvel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.marvel.record.CharactersRecord;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CharactersTest extends AbstractIntegrationTest {

    @Test
    public void crudTest() throws Exception {
        int n = 10;
        List<CharactersRecord> savedCharacters = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            CharactersRecord charactersRecord = generate();
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/public/characters")
                    .content(OBJECT_MAPPER.writeValueAsString(charactersRecord))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(mvcResult -> {
                        CharactersRecord saved = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharactersRecord.class);
                        assertThat(saved.getId()).isNotNull();
                        assertThat(saved.getName()).isEqualTo(charactersRecord.getName());
                        assertThat(saved.getDescription()).isEqualTo(charactersRecord.getDescription());
                        savedCharacters.add(saved);
                    });
        }
        savedCharacters.sort(CHARACTER_RECORD_COMPARATOR);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/characters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<CharactersRecord> charactersRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<CharactersRecord>>() {
                    });
                    charactersRecords.sort(CHARACTER_RECORD_COMPARATOR);
                    assertThat(charactersRecords).usingRecursiveComparison().isEqualTo(savedCharacters);
                });

        CharactersRecord charactersRecord = savedCharacters.get(FAKER.random().nextInt(savedCharacters.size()));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/characters/" + charactersRecord.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    CharactersRecord character = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharactersRecord.class);
                    assertThat(character).usingRecursiveComparison().isEqualTo(charactersRecord);
                });

        charactersRecord.setName(FAKER.superhero().name());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/public/characters/" + charactersRecord.getId())
                .content(OBJECT_MAPPER.writeValueAsString(charactersRecord))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    CharactersRecord character = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharactersRecord.class);
                    assertThat(character).usingRecursiveComparison().isEqualTo(charactersRecord);
                });
    }

    private CharactersRecord generate() {
        CharactersRecord charactersRecord = new CharactersRecord();
        charactersRecord.setName(FAKER.superhero().name());
        charactersRecord.setDescription(FAKER.superhero().descriptor());
        return charactersRecord;
    }

}
