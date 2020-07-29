package com.trelloiii.fotogram.repository.extended.implementation;

import com.trelloiii.fotogram.exceptions.EntityNotFoundException;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.BaseRepository;
import com.trelloiii.fotogram.repository.extended.UserRepositoryExtended;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryExtendedImpl extends BaseRepository implements UserRepositoryExtended {
    private final ConnectionFactory connectionFactory;

    public UserRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<EntityContainer<User>> getUserWithCountOfAllSubs(String username){
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
    private EntityContainer<User> mapProfile(List<Map<String,Object>> rows){
        Map<String,Object> row = rows.get(0);
        User user =  new User(
                (long)row.get("id"),
                (String)row.get("username"),
                null,
                (String)row.get("avatar")
        );
        return new EntityContainer<>(
                user,
                Map.of(
                        "subscribers_count",row.get("subscribers_count"),
                        "subscriptions_count",row.get("subscriptions_count")
                )
        );
    }
}
