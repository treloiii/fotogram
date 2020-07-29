package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.service.PhotoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("/photo")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/{id}")
    public Mono<Photo> getPhotoById(@PathVariable Long id){
        return photoService.getPhotoById(id);
    }
}
