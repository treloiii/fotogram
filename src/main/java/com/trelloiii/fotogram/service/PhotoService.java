package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import com.trelloiii.fotogram.service.facade.PhotoServiceFacade;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoServiceFacade photoServiceFacade;
    private final UploadService uploadService;

    public PhotoService(PhotoRepository photoRepository,
                        PhotoServiceFacade photoServiceFacade,
                        UploadService uploadService) {
        this.photoRepository = photoRepository;
        this.photoServiceFacade = photoServiceFacade;
        this.uploadService = uploadService;
    }

    public Mono<EntityContainer<Photo>> getPhotoById(Long id) {
        return photoRepository.findFullPhotoById(id);
    }

    public Mono<Page<Photo>> getUserPhotos(String username, Pageable pageable) {
        return photoRepository.findByOwnerUsername(username, pageable);
    }

    @SneakyThrows
    public Mono<Photo> uploadAndGet(FilePart file, String caption, long userId) {
        return uploadService.uploadFile(file, userId)
                .flatMap(pair -> {
                    String resultFileName = pair.getSecond();
                    return photoServiceFacade.savePhotoAndGet(userId, caption, resultFileName);
                });
    }

    public void deletePhoto(Long id) {
        photoRepository.findById(id)
                .subscribe(photo -> {
                    uploadService.deleteFile(photo.getUrl());
                    photoRepository.deleteById(id).subscribe();
                });
    }
}
