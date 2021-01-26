package com.marvel.controller;

import com.marvel.exceptions.NotFoundException;
import com.marvel.model.Character;
import com.marvel.model.Comic;
import com.marvel.service.CharacterService;
import com.marvel.service.ComicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@RequestMapping("/characters")
@Api(value = "character_resources", description = "CRUD character")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private ComicService comicService;

    @GetMapping("")
    @ApiOperation(value = "Fetches lists of characters")
    public ResponseEntity<List<Character>> findAll(@QueryParam("name") String name,
                                                   @QueryParam("start") Integer start,
                                                   @QueryParam("size") Integer size,
                                                   @QueryParam("sortedBy") String sortedBy) {

        List<Character> allCharacter = characterService.findAllCharacter();
        if (sortedBy != null) allCharacter = characterService.sortBy(sortedBy);

        if (name != null) allCharacter = characterService.getAllCharactersForName(name, allCharacter);

        if (start != null && size != null)
            allCharacter = characterService.getAllCharactersPaginated(start, size, allCharacter);

        if (null != allCharacter) {
            return ResponseEntity.ok(allCharacter);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Выбирает персонажа по id.")
    public ResponseEntity<Character> findById(@RequestParam int id) {
        Character character = characterService.findById(id);
        if (character != null) {
            return ResponseEntity.ok(character);
        } else {
            throw new NotFoundException();
        }
    }


    @GetMapping("/{characterId}/comics")
    @ApiOperation(value = "Находим списки комиксов отфильтрованных по персонажу id.")
    public ResponseEntity<List<Comic>> findComicsByCharacterId(@RequestParam int id) {
        List<Comic> result = comicService.findByCharacterId(id);
        if (result != null)
            return ResponseEntity.ok(result);
        else throw new NotFoundException();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Сохраняем персонажа.")
    public void saveCharacter(@RequestBody @Valid Character character) {
        characterService.save(character);
    }

}