package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryExtended {
    Flux<User> findAllUsers();
    Mono<User> getUserProfileByTag(String tag);
}
