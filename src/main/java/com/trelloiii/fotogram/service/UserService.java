package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.dto.UserProfileDto;
import com.trelloiii.fotogram.model.dirty.CustomUser;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.PhotoRepository;
import com.trelloiii.fotogram.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    public UserService(UserRepository userRepository, PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
    }

    public Flux<Photo> findAllUserPhotos(String username) {
        return photoRepository.getAllUserPhotos(username);
    }

    public Mono<UserProfileDto> getUserProfile(String username) {
        Mono<CustomUser> customUser = userRepository.getUserProfile(username);
        Flux<Photo> userPhotos = findAllUserPhotos(username);
        return userPhotos.collectList()
                .flatMap(photos-> customUser.flatMap(user->{
                    UserProfileDto userProfileDto = new UserProfileDto(
                            user,
                            photos
                    );
                    return Mono.just(userProfileDto);
                }));
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return userRepository.findUserByUsername(s)
                .cast(UserDetails.class);
    }

    public Mono<User> getUser(String username) {
        return userRepository.findUserByUsername(username);
    }
}
