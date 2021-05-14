package com.marvel.service;

import com.marvel.entity.Character;
import com.marvel.exceptions.NotFoundException;
import com.marvel.record.CharacterRecord;
import com.marvel.repository.CharacterRepository;
import com.marvel.repository.OffsetBasedPage;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public List<CharacterRecord> findAllCharacter(@Nullable String name,
                                                  Integer start,
                                                  Integer size,
                                                  String sortedBy) {
        OffsetBasedPage offsetBasedPage = new OffsetBasedPage(start, size, Sort.by(sortedBy));
        return Optional.ofNullable(name)
                .map(n -> characterRepository.findAllByName(n, offsetBasedPage))
                .orElse(characterRepository.findAll(offsetBasedPage)).stream()
                .map(CharacterRecord::from)
                .collect(Collectors.toList());
    }

    public CharacterRecord findById(Long id) {
        return characterRepository.findById(id)
                .map(CharacterRecord::from)
                .orElseThrow(NotFoundException::new);
    }

    public List<CharacterRecord> findByComicId(Long comicId) {
        return characterRepository.findAllByComics_Id(comicId).stream()
                .map(CharacterRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharacterRecord save(CharacterRecord characterRecord) {
        return CharacterRecord.from(characterRepository.save(fill(new Character(), characterRecord)));
    }

    @Transactional
    public CharacterRecord update(Long id, CharacterRecord newCharacter) {
        Character oldCharacter = characterRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return CharacterRecord.from(characterRepository.save(fill(oldCharacter, newCharacter)));
    }

    private Character fill(Character character, CharacterRecord characterRecord) {
        character.setName(characterRecord.getName());
        character.setDescription(characterRecord.getDescription());
        return character;
    }

}
