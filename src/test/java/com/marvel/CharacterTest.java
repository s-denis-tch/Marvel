package com.marvel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.marvel.record.CharacterRecord;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CharacterTest extends AbstractIntegrationTest {

    @Test
    public void crudTest() throws Exception {
        int n = 10;
        List<CharacterRecord> savedCharacters = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            CharacterRecord characterRecord = generate();
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/public/characters")
                    .content(OBJECT_MAPPER.writeValueAsString(characterRecord))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(mvcResult -> {
                        CharacterRecord saved = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharacterRecord.class);
                        assertThat(saved.getId()).isNotNull();
                        assertThat(saved.getName()).isEqualTo(characterRecord.getName());
                        assertThat(saved.getDescription()).isEqualTo(characterRecord.getDescription());
                        savedCharacters.add(saved);
                    });
        }
        savedCharacters.sort(CHARACTER_RECORD_COMPARATOR);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/characters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<CharacterRecord> characterRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<CharacterRecord>>() {
                    });
                    characterRecords.sort(CHARACTER_RECORD_COMPARATOR);
                    assertThat(characterRecords).usingRecursiveComparison().isEqualTo(savedCharacters);
                });

        CharacterRecord characterRecord = savedCharacters.get(FAKER.random().nextInt(savedCharacters.size()));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/characters/" + characterRecord.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    CharacterRecord character = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharacterRecord.class);
                    assertThat(character).usingRecursiveComparison().isEqualTo(characterRecord);
                });

        characterRecord.setName(FAKER.superhero().name());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/public/characters/" + characterRecord.getId())
                .content(OBJECT_MAPPER.writeValueAsString(characterRecord))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    CharacterRecord character = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharacterRecord.class);
                    assertThat(character).usingRecursiveComparison().isEqualTo(characterRecord);
                });
    }

    private CharacterRecord generate() {
        CharacterRecord characterRecord = new CharacterRecord();
        characterRecord.setName(FAKER.superhero().name());
        characterRecord.setDescription(FAKER.superhero().descriptor());
        return characterRecord;
    }

}
