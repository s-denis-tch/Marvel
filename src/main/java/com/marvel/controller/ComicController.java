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
@RequestMapping("/v1/public/comics")
@Api(value = "comics_resources", description = "CRUD comics")
public class ComicController {

    private static final Logger LOG = LoggerFactory.getLogger(ComicController.class);

    private final ComicService comicService;
    private final CharacterService characterService;

    public ComicController(ComicService comicService,
                           CharacterService characterService) {
        this.comicService = comicService;
        this.characterService = characterService;
    }

    @GetMapping
    @ApiOperation(value = "Возвращает список комиксов")
    public ResponseEntity<List<ComicRecord>> findAll(@RequestParam(value = "title", required = false) String title,
                                                     @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                     @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                                     @RequestParam(value = "sortedBy", required = false, defaultValue = "title") String sortedBy) {
        LOG.info("Запрос на список комиксов");
        List<ComicRecord> comicRecords = comicService.findAllComics(title, start, size, sortedBy);
        return !comicRecords.isEmpty() ? ResponseEntity.ok(comicRecords) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Находит комикс по id")
    public ResponseEntity<ComicRecord> findById(@PathVariable Long id) {
        LOG.info("Запрос на комикс по id");
        return ResponseEntity.ok(comicService.findById(id));
    }

    @GetMapping("/{comicId}/characters")
    @ApiOperation(value = "Находит списки персонажей по комиксу id")
    public ResponseEntity<List<CharacterRecord>> findByComicId(@PathVariable Long comicId) {
        LOG.info("Запрос на списки персонажей по комиксу id");
        List<CharacterRecord> characterRecords = characterService.findByComicId(comicId);
        return !characterRecords.isEmpty() ? ResponseEntity.ok(characterRecords) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Сохраняет комикс")
    public ResponseEntity<ComicRecord> saveComic(@RequestBody @Valid ComicRecord comicRecord) {
        LOG.info("Запрос на сохранение комикса");
        return ResponseEntity.ok(comicService.save(comicRecord));
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновляет комикс")
    public ResponseEntity<ComicRecord> updateComic(@PathVariable Long id,
                                                   @RequestBody @Valid ComicRecord comicRecord) {
        LOG.info("Запрос на обновление комикса");
        return ResponseEntity.ok(comicService.update(id, comicRecord));
    }

}
