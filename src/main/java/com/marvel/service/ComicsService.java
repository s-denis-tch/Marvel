package com.marvel.service;

import com.marvel.entity.Character;
import com.marvel.entity.Comics;
import com.marvel.exceptions.IncorrectFieldException;
import com.marvel.exceptions.NotFoundException;
import com.marvel.record.ComicsRecord;
import com.marvel.repository.CharactersRepository;
import com.marvel.repository.ComicsRepository;
import com.marvel.repository.OffsetBasedPage;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComicsService {

    private final ComicsRepository comicsRepository;
    private final CharactersRepository charactersRepository;

    public ComicsService(ComicsRepository comicsRepository,
                         CharactersRepository charactersRepository) {
        this.comicsRepository = comicsRepository;
        this.charactersRepository = charactersRepository;
    }

    public List<ComicsRecord> findAllComics(@Nullable String title,
                                            Integer start,
                                            Integer size,
                                            String sortedBy) {
        OffsetBasedPage offsetBasedPage = new OffsetBasedPage(start, size, Sort.by(sortedBy));
        return Optional.ofNullable(title)
                .map(n -> comicsRepository.findAllByTitle(n, offsetBasedPage))
                .orElse(comicsRepository.findAll(offsetBasedPage)).stream()
                .map(ComicsRecord::from)
                .collect(Collectors.toList());
    }

    public ComicsRecord findById(Long id) {
        return comicsRepository.findById(id)
                .map(ComicsRecord::from)
                .orElseThrow(NotFoundException::new);
    }

    public List<ComicsRecord> findByCharacterId(Long characterId) {
        return comicsRepository.findAllByCharacters_Id(characterId).stream()
                .map(ComicsRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComicsRecord save(ComicsRecord comicsRecord) {
        return ComicsRecord.from(comicsRepository.save(fill(new Comics(), comicsRecord)));
    }

    @Transactional
    public ComicsRecord update(Long id, ComicsRecord newComic) {
        Comics oldComics = comicsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return ComicsRecord.from(comicsRepository.save(fill(oldComics, newComic)));
    }

    private Comics fill(Comics comics, ComicsRecord comicsRecord) {
        comics.setTitle(comicsRecord.getTitle());
        comics.setDescription(comicsRecord.getDescription());
        List<Character> characters = new ArrayList<>(comicsRecord.getCharacterRecords().size());
        comicsRecord.getCharacterRecords()
                .forEach(characterRecord ->
                        characters.add(
                                Optional.ofNullable(characterRecord.getId())
                                        .map(charactersRepository::findById)
                                        .orElseThrow(() -> new IncorrectFieldException("Id of character shouldn't be null", "id"))
                                        .orElseThrow(NotFoundException::new)
                        )
                );
        comics.setCharacters(characters);
        return comics;
    }

}
