package com.marvel.repository;

import com.marvel.entity.Comics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComicsRepository extends CrudRepository<Comics, Long> {

    List<Comics> findAllByTitle(String title, Pageable pageable);

    List<Comics> findAll(Pageable pageable);

    List<Comics> findAllByCharacters_Id(Long characterId);

}
