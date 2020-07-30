package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface UserRepositoryExtended {
    Mono<EntityContainer<User>> getUserWithCountOfAllSubs(String username);
    Mono<Page<User>> findUserSubscribers(String username, Pageable pageable);
    Mono<Page<User>> findUserSubscriptions(String username, Pageable pageable);
}
