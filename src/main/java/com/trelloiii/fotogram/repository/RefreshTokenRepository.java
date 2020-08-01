package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.UserRefreshToken;
import com.trelloiii.fotogram.repository.extended.RefreshTokenRepositoryExtended;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends ReactiveCrudRepository<UserRefreshToken,Long>, RefreshTokenRepositoryExtended {
    Mono<UserRefreshToken> findByToken(String token);
}
