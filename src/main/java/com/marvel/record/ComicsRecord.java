package com.marvel.record;

import com.marvel.entity.Comics;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComicsRecord {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
    private List<CharactersRecord> charactersRecords = new ArrayList<>();

    public ComicsRecord() {
    }

    public ComicsRecord(Long id, String title, String description, List<CharactersRecord> charactersRecords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.charactersRecords = charactersRecords;
    }

    public static ComicsRecord from(Comics comics) {
        return new ComicsRecord(
                comics.getId(),
                comics.getTitle(),
                comics.getDescription(),
                comics.getCharacters().stream()
                        .map(CharactersRecord::from)
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

    public List<CharactersRecord> getCharacterRecords() {
        return charactersRecords;
    }

    public void setCharacterRecords(List<CharactersRecord> charactersRecords) {
        this.charactersRecords = charactersRecords;
    }

}
