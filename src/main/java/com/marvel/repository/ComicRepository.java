package com.marvel.repository;

import com.marvel.entity.Comic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComicRepository extends CrudRepository<Comic, Long> {

    List<Comic> findAllByTitle(String title, Pageable pageable);

    List<Comic> findAll(Pageable pageable);

    List<Comic> findAllByCharacters_Id(Long characterId);

}
