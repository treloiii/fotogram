package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import reactor.core.publisher.Mono;

public interface UserRepositoryExtended {
    Mono<EntityContainer<User>> getUserWithCountOfAllSubs(String username);
}
