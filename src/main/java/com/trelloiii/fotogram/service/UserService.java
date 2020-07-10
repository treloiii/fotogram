package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    public UserService(UserRepository userRepository, PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
    }

    public Mono<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public Mono<User> getUserByTag(String tag){return userRepository.getUserProfileByTag(tag);}
    public Mono<User> findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }
    public Flux<User> findAllUsers(){
        return userRepository.findAllUsers();
    }
    public Flux<Photo> findAllUserPhotos(String tag){
        return photoRepository.getAllUserPhotos(tag);
    }
}
