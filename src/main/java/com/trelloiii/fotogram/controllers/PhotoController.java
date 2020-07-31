package com.trelloiii.fotogram.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import com.trelloiii.fotogram.service.PhotoService;
import com.trelloiii.fotogram.views.View;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/photo")
public class PhotoController {
    private final PhotoService photoService;
    private final static int PHOTO_PER_PAGE=8;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/{id}")
    public Mono<EntityContainer<Photo>> getPhotoById(@PathVariable Long id){
        return photoService.getPhotoById(id);
    }
    @GetMapping("/user/{username}")
    @JsonView(View.Important.class)
    public Mono<Page<Photo>> getPhotosByUsername(
            @PathVariable String username,
            @PageableDefault(size = PHOTO_PER_PAGE,direction = Sort.Direction.DESC,sort = {"id"}) Pageable pageable){
        return photoService.getUserPhotos(username, pageable);
    }

//    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Mono<Photo> postPhoto(@RequestParam("file") Part part){
//        //todo переписать на ебучий функшионал код
//        System.out.println("susi");
//        return null;
//    }
}
