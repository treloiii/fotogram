package com.trelloiii.fotogram.repository.extended.implementation;

import com.trelloiii.fotogram.model.UserRefreshToken;
import com.trelloiii.fotogram.repository.BaseRepository;
import com.trelloiii.fotogram.repository.extended.RefreshTokenRepositoryExtended;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;

import java.util.Map;

public class RefreshTokenRepositoryExtendedImpl extends BaseRepository implements RefreshTokenRepositoryExtended {
    private final Logger logger = LoggerFactory.getLogger(RefreshTokenRepositoryExtendedImpl.class);
    private final ConnectionFactory connectionFactory;
    public RefreshTokenRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void saveByUserId(Long id, String token) {
        queryMap(
                "update user_refresh_token u set u.token = ?token where u.user_id = ?id;",
                connectionFactory,
                Map.of("token",token,"id",id)
        ).subscribe(affectedRows->logger.info("Token saved. Rows affected {}",affectedRows));
    }
}
