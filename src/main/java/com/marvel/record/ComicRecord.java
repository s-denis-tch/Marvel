package com.marvel.record;

import com.marvel.entity.Comic;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComicRecord {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
    private List<CharacterRecord> characterRecords = new ArrayList<>();

    public ComicRecord() {
    }

    public ComicRecord(Long id, String title, String description, List<CharacterRecord> characterRecords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.characterRecords = characterRecords;
    }

    public static ComicRecord from(Comic comic) {
        return new ComicRecord(
                comic.getId(),
                comic.getTitle(),
                comic.getDescription(),
                comic.getCharacters().stream()
                        .map(CharacterRecord::from)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CharacterRecord> getCharacterRecords() {
        return characterRecords;
    }

    public void setCharacterRecords(List<CharacterRecord> characterRecords) {
        this.characterRecords = characterRecords;
    }

}
