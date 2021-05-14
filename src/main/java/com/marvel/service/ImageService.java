package com.marvel.service;

import com.marvel.entity.Image;
import com.marvel.exceptions.NotFoundException;
import com.marvel.record.ImageRecord;
import com.marvel.repository.ImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageRecord save(ImageRecord imageRecord) {
        Image image = new Image();
        image.setName(imageRecord.getName());
        image.setType(imageRecord.getType());
        image.setData(imageRecord.getData());
        return ImageRecord.from(imageRepository.save(image));
    }

    public ImageRecord findByName(String name) {
        return imageRepository.findByName(name)
                .map(ImageRecord::from)
                .orElseThrow(NotFoundException::new);
    }

}
