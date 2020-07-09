package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface UserRepository extends ReactiveCrudRepository<User,Long>, UserRepositoryExtended {
    Mono<User> findUserByUsername(String username);
    Mono<User> findUserByTag(String tag);
}
