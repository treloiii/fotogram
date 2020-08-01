package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.UserRefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepositoryExtended {
    void saveByUserId(Long id, String token);
}
