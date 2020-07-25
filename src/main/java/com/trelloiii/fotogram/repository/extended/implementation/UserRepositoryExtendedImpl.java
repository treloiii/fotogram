package com.trelloiii.fotogram.repository.extended.implementation;

import com.trelloiii.fotogram.model.dirty.CustomUser;
import com.trelloiii.fotogram.exceptions.EntityNotFoundException;
import com.trelloiii.fotogram.repository.BaseRepository;
import com.trelloiii.fotogram.repository.extended.UserRepositoryExtended;
import io.r2dbc.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class UserRepositoryExtendedImpl extends BaseRepository implements UserRepositoryExtended {
    private final ConnectionFactory connectionFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<CustomUser> getUserProfile(String username){
        return queryMapRows(
            "select id\n" +
                    "     , username\n" +
                    "     , avatar_url as avatar\n" +
                    "     , (select count(*) from subs subscribers where subscribers.subscribe_on = u.id) as subscribers_count\n" +
                    "     , (select count(*) from subs subscribptions where subscribptions.user = u.id) as subscriptions_count\n" +
                    "from usr u\n" +
                    "where u.username = ?username",
                connectionFactory,
                Map.of("username",username)
        )
                .flatMap(rows->{
                    if(rows.size()<1){
                        return Mono.error(new EntityNotFoundException(
                                String.format("User with username %s not found",username)
                        ));
                    }
                    return Mono.just(
                            mapProfile(rows)
                    );
                });
    }
    private CustomUser mapProfile(List<Map<String,Object>> rows){
        Map<String,Object> row = rows.get(0);
        return new CustomUser(
                (long)row.get("id"),
                (String)row.get("username"),
                (String)row.get("avatar"),
                (long)row.get("subscribers_count"),
                (long)row.get("subscriptions_count")
        );
    }
}
