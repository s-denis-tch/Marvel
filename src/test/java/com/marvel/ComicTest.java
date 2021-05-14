package com.marvel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.marvel.record.CharacterRecord;
import com.marvel.record.ComicRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ComicTest extends AbstractIntegrationTest {

    private List<CharacterRecord> characters;

    @BeforeEach
    public void beforeEach() throws Exception {
        int n = 10;
        characters = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/public/characters")
                    .content(OBJECT_MAPPER.writeValueAsString(generateCharacterRecord()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(mvcResult -> characters.add(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharacterRecord.class)));
        }
    }

    @Test
    public void crudTest() throws Exception {
        int n = 10;
        List<ComicRecord> savedComics = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ComicRecord comicRecord = generate();
            comicRecord.getCharacterRecords().sort(CHARACTER_RECORD_COMPARATOR);
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/public/comics")
                    .content(OBJECT_MAPPER.writeValueAsString(comicRecord))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(mvcResult -> {
                        ComicRecord saved = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicRecord.class);
                        assertThat(saved.getId()).isNotNull();
                        assertThat(saved.getTitle()).isEqualTo(comicRecord.getTitle());
                        assertThat(saved.getDescription()).isEqualTo(comicRecord.getDescription());
                        saved.getCharacterRecords().sort(CHARACTER_RECORD_COMPARATOR);
                        assertThat(saved.getCharacterRecords()).usingRecursiveComparison().ignoringFields("comicIds").isEqualTo(comicRecord.getCharacterRecords());
                        savedComics.add(saved);
                    });
        }

        savedComics.sort(COMIC_RECORD_COMPARATOR);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<ComicRecord> comicRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<ComicRecord>>() {
                    });
                    comicRecords.sort(COMIC_RECORD_COMPARATOR);
                    assertThat(comicRecords).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(savedComics);
                });

        ComicRecord comicRecord = savedComics.get(FAKER.random().nextInt(savedComics.size()));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics/" + comicRecord.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    ComicRecord comic = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicRecord.class);
                    assertThat(comic).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(comicRecord);
                });

        comicRecord.setTitle(FAKER.harryPotter().book());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/public/comics/" + comicRecord.getId())
                .content(OBJECT_MAPPER.writeValueAsString(comicRecord))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    ComicRecord comic = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicRecord.class);
                    assertThat(comic).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(comicRecord);
                });

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics/" + comicRecord.getId() + "/characters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<CharacterRecord> characterRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<CharacterRecord>>() {
                    });
                    characterRecords.sort(CHARACTER_RECORD_COMPARATOR);
                    assertThat(characterRecords).usingRecursiveComparison().ignoringFields("comicIds").isEqualTo(comicRecord.getCharacterRecords());
                });
    }

    private CharacterRecord generateCharacterRecord() {
        CharacterRecord characterRecord = new CharacterRecord();
        characterRecord.setName(FAKER.superhero().name());
        characterRecord.setDescription(FAKER.superhero().descriptor());
        return characterRecord;
    }

    private ComicRecord generate() {
        ComicRecord comicRecord = new ComicRecord();
        comicRecord.setTitle(FAKER.harryPotter().book());
        comicRecord.setDescription(FAKER.harryPotter().spell());
        comicRecord.setCharacterRecords(
                Stream.generate(() -> characters.get(FAKER.random().nextInt(characters.size())))
                        .limit(FAKER.random().nextInt(characters.size()))
                        .collect(Collectors.toList())
        );
        return comicRecord;
    }

}
