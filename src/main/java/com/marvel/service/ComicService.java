package com.marvel.service;

import com.marvel.model.Comic;
import com.marvel.repository.ComicRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComicService {

    private final ComicRepository comicRepository;

    public ComicService(ComicRepository comicRepository) {

        this.comicRepository = comicRepository;
    }


    public List<Comic> findAllComics() {
        return comicRepository.findAll();
    }

    public Comic findById(int id) {
        Optional<Comic> optionalComic = comicRepository.findById(id);
        return optionalComic.orElse(null);
    }

    public List<Comic> findByCharacterId(int characterId) {
        List<Comic> comics = comicRepository.findByCharacterId(characterId);
        return comics;
    }

    public List<Comic> getAllComicsForTitle(String title, List<Comic> comics) {
        List<Comic> allComicsForTitle = comics;
        return allComicsForTitle.stream().filter(comic -> comic.getTitle().equals(title)).collect(Collectors.toList());
    }


    public List<Comic> getAllComicsPaginated(int start, int size, List<Comic> allComics) {
        List<Comic> paginated = allComics;
        if (start + size > paginated.size()) return new ArrayList<>();
        return paginated.subList(start, start + size);
    }


    public Comic save(Comic comic) {
        return comicRepository.save(comic);
    }

    public List<Comic> sortBy(String sortedBy) {
        Sort sortOrder = Sort.by(sortedBy);
        return comicRepository.findAll(sortOrder);
    }
}