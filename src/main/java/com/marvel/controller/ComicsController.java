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
@RequestMapping("/v1/public/comics")
@Api(value = "comics_resources", description = "CRUD comics")
public class ComicsController {

    private static final Logger LOG = LoggerFactory.getLogger(ComicsController.class);

    private final ComicsService comicsService;
    private final CharactersService charactersService;

    public ComicsController(ComicsService comicsService,
                            CharactersService charactersService) {
        this.comicsService = comicsService;
        this.charactersService = charactersService;
    }

    @GetMapping
    @ApiOperation(value = "Возвращает список комиксов")
    public ResponseEntity<List<ComicsRecord>> findAll(@RequestParam(value = "title", required = false) String title,
                                                      @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                      @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                                      @RequestParam(value = "sortedBy", required = false, defaultValue = "title") String sortedBy) {
        LOG.info("Запрос на список комиксов");
        List<ComicsRecord> comicsRecords = comicsService.findAllComics(title, start, size, sortedBy);
        return !comicsRecords.isEmpty() ? ResponseEntity.ok(comicsRecords) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Находит комикс по id")
    public ResponseEntity<ComicsRecord> findById(@PathVariable Long id) {
        LOG.info("Запрос на комикс по id");
        return ResponseEntity.ok(comicsService.findById(id));
    }

    @GetMapping("/{comicId}/characters")
    @ApiOperation(value = "Находит списки персонажей по комиксу id")
    public ResponseEntity<List<CharactersRecord>> findByComicId(@PathVariable Long comicId) {
        LOG.info("Запрос на списки персонажей по комиксу id");
        List<CharactersRecord> charactersRecords = charactersService.findByComicId(comicId);
        return !charactersRecords.isEmpty() ? ResponseEntity.ok(charactersRecords) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Сохраняет комикс")
    public ResponseEntity<ComicsRecord> saveComic(@RequestBody @Valid ComicsRecord comicsRecord) {
        LOG.info("Запрос на сохранение комикса");
        return ResponseEntity.ok(comicsService.save(comicsRecord));
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновляет комикс")
    public ResponseEntity<ComicsRecord> updateComic(@PathVariable Long id,
                                                    @RequestBody @Valid ComicsRecord comicsRecord) {
        LOG.info("Запрос на обновление комикса");
        return ResponseEntity.ok(comicsService.update(id, comicsRecord));
    }

}
