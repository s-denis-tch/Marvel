package com.marvel.record;

import com.marvel.entity.Character;
import com.marvel.entity.Comics;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CharactersRecord {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private List<Long> comicIds = new ArrayList<>();

    public CharactersRecord() {
    }

    public CharactersRecord(Long id, String name, String description, List<Long> comicIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.comicIds = comicIds;
    }

    public static CharactersRecord from(Character character) {
        return new CharactersRecord(
                character.getId(),
                character.getName(),
                character.getDescription(),
                character.getComics().stream()
                        .map(Comics::getId)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getComicIds() {
        return comicIds;
    }

    public void setComicIds(List<Long> comicIds) {
        this.comicIds = comicIds;
    }

}
