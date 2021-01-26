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
@RequestMapping("/comics")
@Api(value = "comics_resources", description = "CRUD comics")
public class ComicController {

    @Autowired
    private ComicService comicService;
    @Autowired
    private CharacterService characterService;

    @GetMapping("")
    @ApiOperation(value = "Fetches lists of comics.")
    public ResponseEntity<List<Comic>> findAll(@QueryParam("title") String title,
                                               @QueryParam("start") Integer start,
                                               @QueryParam("size") Integer size,
                                               @QueryParam("sortedBy") String sortedBy) {

        List<Comic> allComics = comicService.findAllComics();
        if (sortedBy != null) allComics = comicService.sortBy(sortedBy);

        if (title != null) allComics = comicService.getAllComicsForTitle(title, allComics);

        if (start != null && size != null) allComics = comicService.getAllComicsPaginated(start, size, allComics);

        if (allComics != null) {
            return ResponseEntity.ok(allComics);
        } else {
            throw new NotFoundException();
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Находим комикс по id.")
    public ResponseEntity<Comic> findById(@RequestParam int id) {
        Comic comic = comicService.findById(id);
        if (comic != null) {
            return ResponseEntity.ok(comic);
        } else {
            throw new NotFoundException();
        }
    }


    @GetMapping("/{comicId}/characters")
    @ApiOperation(value = "Находим списки персонажей по комиксу id.")
    public ResponseEntity<List<Character>> findByComicId(@RequestParam int id) {
        List<Character> result = characterService.findByComicId(id);
        if (result != null)
            return ResponseEntity.ok(result);
        else throw new NotFoundException();
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Сохраняем комикс.")
    public void saveComic(@RequestBody @Valid Comic comic) {
        comicService.save(comic);
    }

}