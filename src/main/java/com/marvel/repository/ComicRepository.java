package com.marvel.repository;

import com.marvel.model.Character;
import com.marvel.model.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComicRepository  extends JpaRepository<Comic, Integer> {

    @Query(value = "SELECT comic.id, comic.title, comic.description," +
            " comic.modified" +
            " FROM comic " +
            " JOIN comic_characters on comic_characters.comic_id = comic.id" +
            " where comic_characters.characters_id=:id " +
            ";", nativeQuery = true)
    List<Comic> findByCharacterId(@Param("id") int id);
}
