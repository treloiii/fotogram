package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Mono<EntityContainer<Photo>> getPhotoById(Long id){
        return photoRepository.findFullPhotoById(id);
    }
    public Mono<Page<Photo>> getUserPhotos(String username, Pageable pageable){
        return photoRepository.findByOwnerUsername(username,pageable);
    }
}
