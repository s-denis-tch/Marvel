package com.marvel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.marvel.record.CharactersRecord;
import com.marvel.record.ComicsRecord;
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

public class ComicsTest extends AbstractIntegrationTest {

    private List<CharactersRecord> characters;

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
                    .andExpect(mvcResult -> characters.add(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CharactersRecord.class)));
        }
    }

    @Test
    public void crudTest() throws Exception {
        int n = 10;
        List<ComicsRecord> savedComics = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ComicsRecord comicsRecord = generate();
            comicsRecord.getCharacterRecords().sort(CHARACTER_RECORD_COMPARATOR);
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/public/comics")
                    .content(OBJECT_MAPPER.writeValueAsString(comicsRecord))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(mvcResult -> {
                        ComicsRecord saved = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicsRecord.class);
                        assertThat(saved.getId()).isNotNull();
                        assertThat(saved.getTitle()).isEqualTo(comicsRecord.getTitle());
                        assertThat(saved.getDescription()).isEqualTo(comicsRecord.getDescription());
                        saved.getCharacterRecords().sort(CHARACTER_RECORD_COMPARATOR);
                        assertThat(saved.getCharacterRecords()).usingRecursiveComparison().ignoringFields("comicIds").isEqualTo(comicsRecord.getCharacterRecords());
                        savedComics.add(saved);
                    });
        }

        savedComics.sort(COMIC_RECORD_COMPARATOR);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<ComicsRecord> comicsRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<ComicsRecord>>() {
                    });
                    comicsRecords.sort(COMIC_RECORD_COMPARATOR);
                    assertThat(comicsRecords).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(savedComics);
                });

        ComicsRecord comicsRecord = savedComics.get(FAKER.random().nextInt(savedComics.size()));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics/" + comicsRecord.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    ComicsRecord comic = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicsRecord.class);
                    assertThat(comic).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(comicsRecord);
                });

        comicsRecord.setTitle(FAKER.harryPotter().book());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/public/comics/" + comicsRecord.getId())
                .content(OBJECT_MAPPER.writeValueAsString(comicsRecord))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    ComicsRecord comic = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ComicsRecord.class);
                    assertThat(comic).usingRecursiveComparison().ignoringFields("characterRecords.comicIds").isEqualTo(comicsRecord);
                });

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/public/comics/" + comicsRecord.getId() + "/characters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<CharactersRecord> charactersRecords = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<CharactersRecord>>() {
                    });
                    charactersRecords.sort(CHARACTER_RECORD_COMPARATOR);
                    assertThat(charactersRecords).usingRecursiveComparison().ignoringFields("comicIds").isEqualTo(comicsRecord.getCharacterRecords());
                });
    }

    private CharactersRecord generateCharacterRecord() {
        CharactersRecord charactersRecord = new CharactersRecord();
        charactersRecord.setName(FAKER.superhero().name());
        charactersRecord.setDescription(FAKER.superhero().descriptor());
        return charactersRecord;
    }

    private ComicsRecord generate() {
        ComicsRecord comicsRecord = new ComicsRecord();
        comicsRecord.setTitle(FAKER.harryPotter().book());
        comicsRecord.setDescription(FAKER.harryPotter().spell());
        comicsRecord.setCharacterRecords(
                Stream.generate(() -> characters.get(FAKER.random().nextInt(characters.size())))
                        .limit(FAKER.random().nextInt(characters.size()))
                        .collect(Collectors.toList())
        );
        return comicsRecord;
    }

}
