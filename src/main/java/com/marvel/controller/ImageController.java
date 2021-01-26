package com.marvel.controller;

import com.marvel.exceptions.NotFoundException;
import com.marvel.model.Image;
import com.marvel.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(path = "image")
@Api(value = "image_resources")
public class ImageController {

    @Autowired
    ImageService imageService;

    @PostMapping("/upload")
    @ApiOperation(value = "upload image.")
    public BodyBuilder uploadImage(@RequestParam("imageFile") MultipartFile file) throws IOException {
        Image img = new Image(file.getOriginalFilename(), file.getContentType(),
                file.getBytes());

        imageService.save(img);
        return ResponseEntity.status(HttpStatus.OK);
    }

    @GetMapping(path = {"/get/{imageName}"})
    @ApiOperation(value = "Получаем изображение.")
    public Image getImage(@PathVariable("imageName") String imageName) throws IOException {

        final Optional<Image> retrievedImage = imageService.findByName(imageName);
        if (retrievedImage.isPresent()) {
            Image img = new Image(retrievedImage.get().getName(), retrievedImage.get().getType(),
                    retrievedImage.get().getData());
            return img;
        } else {
            throw new NotFoundException();
        }

    }
}