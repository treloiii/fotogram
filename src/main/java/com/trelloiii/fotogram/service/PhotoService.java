package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Mono<Photo> getPhotoById(Long id){
        return photoRepository.findById(id);
    }
}
