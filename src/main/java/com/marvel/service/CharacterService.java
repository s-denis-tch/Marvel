package com.marvel.service;

import com.marvel.model.Character;
import com.marvel.repository.CharacterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CharacterService {

    private  final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {

        this.characterRepository = characterRepository;
    }


    public List<Character> findAllCharacter(){

        return characterRepository.findAll();
    }

    public Character findById(int id){
        Optional<Character> optionalCharacter = characterRepository.findById(id);
        return optionalCharacter.orElse(null);
    }

    public List<Character> findByComicId(int comicId){
        List<Character> optionalCharacter = characterRepository.findByComicId(comicId);
        return optionalCharacter;
    }

    public List<Character> getAllCharactersForName(String name, List<Character> characterList){
        List<Character> allCharactersForName = characterList;
        return allCharactersForName.stream().filter(character -> character.getName().equals(name)).collect(Collectors.toList());
    }


    public List<Character> getAllCharactersPaginated(int start, int size, List<Character> characters){
        List<Character> paginated = characters;
        if(start+size>paginated.size()) return new ArrayList<Character>();
        return paginated.subList(start, start+size);

    }

    public Character save(Character character){

        return characterRepository.save(character);
    }

    public List<Character> sortBy(String sortBy) {
        Sort sortOrder = Sort.by(sortBy);
        return characterRepository.findAll(sortOrder);
    }
}