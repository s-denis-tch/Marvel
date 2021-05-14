package com.marvel.controller;

import com.marvel.record.CharacterRecord;
import com.marvel.record.ComicRecord;
import com.marvel.service.CharacterService;
import com.marvel.service.ComicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/public/characters")
@Api(value = "character_resources", description = "CRUD character")
public class CharacterController {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterController.class);

    private final CharacterService characterService;
    private final ComicService comicService;

    public CharacterController(CharacterService characterService,
                               ComicService comicService) {
        this.characterService = characterService;
        this.comicService = comicService;
    }

    @GetMapping
    @ApiOperation(value = "Возвращает список персонажей")
    public ResponseEntity<List<CharacterRecord>> findAll(@RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                                         @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                         @RequestParam(value = "sortedBy", required = false, defaultValue = "name") String sortedBy) {
        LOG.info("Запрос на список персонажей");
        List<CharacterRecord> characterRecords = characterService.findAllCharacter(name, start, size, sortedBy);
        return !characterRecords.isEmpty() ? ResponseEntity.ok(characterRecords) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Выбирает персонажа по id")
    public ResponseEntity<CharacterRecord> findById(@PathVariable Long id) {
        LOG.info("Запрос на персонажа по id");
        return ResponseEntity.ok(characterService.findById(id));
    }

    @GetMapping("/{characterId}/comics")
    @ApiOperation(value = "Находит списки комиксов с персонажем по id")
    public ResponseEntity<List<ComicRecord>> findComicsByCharacterId(@PathVariable Long characterId) {
        LOG.info("Запрос на списки комиксов с персонажем по id");
        List<ComicRecord> comicRecords = comicService.findByCharacterId(characterId);
        return !comicRecords.isEmpty() ? ResponseEntity.ok(comicRecords) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Сохраняет персонажа")
    public ResponseEntity<CharacterRecord> saveCharacter(@RequestBody @Valid CharacterRecord character) {
        LOG.info("Запрос на сохранение персонажа");
        return ResponseEntity.ok(characterService.save(character));
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновляет персонажа")
    public ResponseEntity<CharacterRecord> updateCharacter(@PathVariable Long id,
                                                           @RequestBody @Valid CharacterRecord character) {
        LOG.info("Запрос на обновление персонажа");
        return ResponseEntity.ok(characterService.update(id, character));
    }

}
