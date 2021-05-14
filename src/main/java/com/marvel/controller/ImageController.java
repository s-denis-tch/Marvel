package com.marvel.controller;

import com.marvel.record.ImageRecord;
import com.marvel.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/v1/public/image")
@Api(value = "image_resources")
public class ImageController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    @ApiOperation(value = "Загрузка изображения")
    public ResponseEntity<ImageRecord> uploadImage(@RequestParam("imageFile") MultipartFile file) throws IOException {
        LOG.info("Запрос на загрузку изображения");
        return ResponseEntity.ok(imageService.save(new ImageRecord(file.getOriginalFilename(), file.getContentType(), file.getBytes())));
    }

    @GetMapping(path = {"/get/{imageName}"})
    @ApiOperation(value = "Получает изображение")
    public ResponseEntity<ImageRecord> getImage(@PathVariable("imageName") String imageName) {
        LOG.info("Запрос на получение изображения");
        return ResponseEntity.ok(imageService.findByName(imageName));
    }

}
