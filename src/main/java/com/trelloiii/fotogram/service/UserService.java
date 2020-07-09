package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public Mono<User> findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }
    public Mono<User> findUserByTag(String tag){
        return userRepository.findUserByTag(tag);
    }
    public Flux<User> findAllUsers(){
        return userRepository.findAllUsers();
    }
}
