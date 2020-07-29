package com.trelloiii.fotogram.service.facade;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.UserRepository;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class UserServiceFacade {
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    public UserServiceFacade(UserRepository userRepository, PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
    }

    public Mono<Tuple2<EntityContainer<User>, Page<Photo>>> getUserAndHisPhotos(String username, Pageable pageable){
        Mono<EntityContainer<User>> customUser = userRepository.getUserWithCountOfAllSubs(username);
        Mono<Page<Photo>> userPhotos = photoRepository.findByOwnerUsername(username,pageable);
        return Mono.zip(customUser,userPhotos);
    }
}
