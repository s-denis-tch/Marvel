package com.marvel.service;

import com.marvel.entity.Character;
import com.marvel.exceptions.NotFoundException;
import com.marvel.record.CharactersRecord;
import com.marvel.repository.CharactersRepository;
import com.marvel.repository.OffsetBasedPage;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CharactersService {

    private final CharactersRepository charactersRepository;

    public CharactersService(CharactersRepository charactersRepository) {
        this.charactersRepository = charactersRepository;
    }

    public List<CharactersRecord> findAllCharacter(@Nullable String name,
                                                   Integer start,
                                                   Integer size,
                                                   String sortedBy) {
        OffsetBasedPage offsetBasedPage = new OffsetBasedPage(start, size, Sort.by(sortedBy));
        return Optional.ofNullable(name)
                .map(n -> charactersRepository.findAllByName(n, offsetBasedPage))
                .orElse(charactersRepository.findAll(offsetBasedPage)).stream()
                .map(CharactersRecord::from)
                .collect(Collectors.toList());
    }

    public CharactersRecord findById(Long id) {
        return charactersRepository.findById(id)
                .map(CharactersRecord::from)
                .orElseThrow(NotFoundException::new);
    }

    public List<CharactersRecord> findByComicId(Long comicId) {
        return charactersRepository.findAllByComics_Id(comicId).stream()
                .map(CharactersRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharactersRecord save(CharactersRecord charactersRecord) {
        return CharactersRecord.from(charactersRepository.save(fill(new Character(), charactersRecord)));
    }

    @Transactional
    public CharactersRecord update(Long id, CharactersRecord newCharacter) {
        Character oldCharacter = charactersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return CharactersRecord.from(charactersRepository.save(fill(oldCharacter, newCharacter)));
    }

    private Character fill(Character character, CharactersRecord charactersRecord) {
        character.setName(charactersRecord.getName());
        character.setDescription(charactersRecord.getDescription());
        return character;
    }

}
