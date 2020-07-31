package com.trelloiii.fotogram.service.facade;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class PhotoServiceFacade {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    @Value("${domain}")
    private String domain;
    public PhotoServiceFacade(PhotoRepository photoRepository, UserRepository userRepository) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    public Mono<Photo> savePhotoAndGet(long id,String caption,String photoName){
        return userRepository.findById(id)
                .flatMap(user->{
                    Photo photo = new Photo();
                    photo.setOwner(user);
                    photo.setCaption(caption);
                    photo.setTime(LocalDate.now());
                    photo.setUrl(domain+"img/"+photoName);
                    photo.setOwnerId(id);
                    return photoRepository.save(photo);
                });
    }
}
