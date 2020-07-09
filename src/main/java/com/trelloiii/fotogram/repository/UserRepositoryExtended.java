package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.User;
import reactor.core.publisher.Flux;

public interface UserRepositoryExtended {
    Flux<User> findAllUsers();
}
