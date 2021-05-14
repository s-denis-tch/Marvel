package com.marvel.service;

import com.marvel.entity.Character;
import com.marvel.entity.Comic;
import com.marvel.exceptions.IncorrectFieldException;
import com.marvel.exceptions.NotFoundException;
import com.marvel.record.ComicRecord;
import com.marvel.repository.CharacterRepository;
import com.marvel.repository.ComicRepository;
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
public class ComicService {

    private final ComicRepository comicRepository;
    private final CharacterRepository characterRepository;

    public ComicService(ComicRepository comicRepository,
                        CharacterRepository characterRepository) {
        this.comicRepository = comicRepository;
        this.characterRepository = characterRepository;
    }

    public List<ComicRecord> findAllComics(@Nullable String title,
                                           Integer start,
                                           Integer size,
                                           String sortedBy) {
        OffsetBasedPage offsetBasedPage = new OffsetBasedPage(start, size, Sort.by(sortedBy));
        return Optional.ofNullable(title)
                .map(n -> comicRepository.findAllByTitle(n, offsetBasedPage))
                .orElse(comicRepository.findAll(offsetBasedPage)).stream()
                .map(ComicRecord::from)
                .collect(Collectors.toList());
    }

    public ComicRecord findById(Long id) {
        return comicRepository.findById(id)
                .map(ComicRecord::from)
                .orElseThrow(NotFoundException::new);
    }

    public List<ComicRecord> findByCharacterId(Long characterId) {
        return comicRepository.findAllByCharacters_Id(characterId).stream()
                .map(ComicRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComicRecord save(ComicRecord comicRecord) {
        return ComicRecord.from(comicRepository.save(fill(new Comic(), comicRecord)));
    }

    @Transactional
    public ComicRecord update(Long id, ComicRecord newComic) {
        Comic oldComic = comicRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return ComicRecord.from(comicRepository.save(fill(oldComic, newComic)));
    }

    private Comic fill(Comic comic, ComicRecord comicRecord) {
        comic.setTitle(comicRecord.getTitle());
        comic.setDescription(comicRecord.getDescription());
        List<Character> characters = new ArrayList<>(comicRecord.getCharacterRecords().size());
        comicRecord.getCharacterRecords()
                .forEach(characterRecord ->
                        characters.add(
                                Optional.ofNullable(characterRecord.getId())
                                        .map(characterRepository::findById)
                                        .orElseThrow(() -> new IncorrectFieldException("Id of character shouldn't be null", "id"))
                                        .orElseThrow(NotFoundException::new)
                        )
                );
        comic.setCharacters(characters);
        return comic;
    }

}
