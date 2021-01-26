package com.marvel.service;

import com.marvel.model.Image;
import com.marvel.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {

        this.imageRepository = imageRepository;
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public Optional<Image> findByName(String name) {

        return imageRepository.findByName(name);
    }

}