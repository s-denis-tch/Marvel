package com.marvel.repository;

import com.marvel.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Integer> {

    @Query(value = "SELECT character.id, character.name, character.description," +
            " character.modified" +
            " FROM character" +
            " JOIN comic_characters on comic_characters.characters_id = character.id" +
            " where comic_characters.comic_id=:id " +
            ";", nativeQuery = true)
    List<Character> findByComicId(@Param("id") int id);
}