package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.extended.UserRepositoryExtended;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,Long>, UserRepositoryExtended {
    Mono<User> findUserByUsername(String s);
}
