package com.marvel.repository;

import com.marvel.entity.Character;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CharactersRepository extends CrudRepository<Character, Long> {

    List<Character> findAllByName(String name, Pageable pageable);

    List<Character> findAll(Pageable pageable);

    List<Character> findAllByComics_Id(Long comicId);

}
