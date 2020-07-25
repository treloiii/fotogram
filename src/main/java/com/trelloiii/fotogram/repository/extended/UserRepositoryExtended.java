package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.dirty.CustomUser;
import reactor.core.publisher.Mono;

public interface UserRepositoryExtended {
    Mono<CustomUser> getUserProfile(String username);
}
