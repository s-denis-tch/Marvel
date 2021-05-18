package com.marvel.controller;

import com.marvel.record.CharactersRecord;
import com.marvel.record.ComicsRecord;
import com.marvel.service.CharactersService;
import com.marvel.service.ComicsService;
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
public class CharactersController {

    private static final Logger LOG = LoggerFactory.getLogger(CharactersController.class);

    private final CharactersService charactersService;
    private final ComicsService comicsService;

    public CharactersController(CharactersService charactersService,
                                ComicsService comicsService) {
        this.charactersService = charactersService;
        this.comicsService = comicsService;
    }

    @GetMapping
    @ApiOperation(value = "Возвращает список персонажей")
    public ResponseEntity<List<CharactersRecord>> findAll(@RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                                          @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                          @RequestParam(value = "sortedBy", required = false, defaultValue = "name") String sortedBy) {
        LOG.info("Запрос на список персонажей");
        List<CharactersRecord> charactersRecords = charactersService.findAllCharacter(name, start, size, sortedBy);
        return !charactersRecords.isEmpty() ? ResponseEntity.ok(charactersRecords) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Выбирает персонажа по id")
    public ResponseEntity<CharactersRecord> findById(@PathVariable Long id) {
        LOG.info("Запрос на персонажа по id");
        return ResponseEntity.ok(charactersService.findById(id));
    }

    @GetMapping("/{characterId}/comics")
    @ApiOperation(value = "Находит списки комиксов с персонажем по id")
    public ResponseEntity<List<ComicsRecord>> findComicsByCharacterId(@PathVariable Long characterId) {
        LOG.info("Запрос на списки комиксов с персонажем по id");
        List<ComicsRecord> comicsRecords = comicsService.findByCharacterId(characterId);
        return !comicsRecords.isEmpty() ? ResponseEntity.ok(comicsRecords) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Сохраняет персонажа")
    public ResponseEntity<CharactersRecord> saveCharacter(@RequestBody @Valid CharactersRecord character) {
        LOG.info("Запрос на сохранение персонажа");
        return ResponseEntity.ok(charactersService.save(character));
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновляет персонажа")
    public ResponseEntity<CharactersRecord> updateCharacter(@PathVariable Long id,
                                                            @RequestBody @Valid CharactersRecord character) {
        LOG.info("Запрос на обновление персонажа");
        return ResponseEntity.ok(charactersService.update(id, character));
    }

}
