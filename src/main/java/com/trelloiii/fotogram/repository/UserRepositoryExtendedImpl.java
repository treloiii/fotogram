package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.dto.UserSubs;
import com.trelloiii.fotogram.exceptions.EntityNotFoundException;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Repository
public class UserRepositoryExtendedImpl extends BaseRepository implements UserRepositoryExtended {
    private final ConnectionFactory connectionFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<User> getUserProfileByTag(String tag) {
        return queryMapRows(
                "select distinct  u.id, u.username,u.tag, u.avatar_url,\n" +
                        "       subscription.id as subscription_id , subscription.avatar_url as subcription_avatar , subscription.tag as subscription_tag,\n" +
                        "        subscriber.id as subscriber_id , subscriber.avatar_url as subscriber_avatar , subscriber.tag as subscriber_tag from usr u\n" +
                        "left join usr subscription on subscription.id in (select distinct subs.subscribe_on from subs where subs.user = 1)\n" +
                        "left join usr subscriber on subscriber.id in (select distinct subs.user from subs where subs.subscribe_on = 1)\n" +
                        "where u.tag = ?tag;",
                connectionFactory,
                Map.of("tag", tag)
        )
                .flatMap(rows -> {
                    UserSubs userSubs = new UserSubs();
                    rows.forEach(row -> {
                        User user = userFromRow(row);

                        User subscriber = new User();
                        subscriber.setId((Long) row.get("subscriber_id"));
                        subscriber.setTag((String) row.get("subscriber_tag"));
                        subscriber.setAvatarUrl((String) row.get("subscriber_avatar"));

                        User subscription = new User();
                        subscription.setId((Long) row.get("subscription_id"));
                        subscription.setTag((String) row.get("subscription_tag"));
                        subscription.setAvatarUrl((String) row.get("subscription_avatar"));

                        userSubs.addUserIfNotPresent(user);
                        userSubs.addSubscriber(subscriber);
                        userSubs.addSubscription(subscription);
                    });
                    return Mono.just(userSubs.mapUser());
                });
    }

    @Override
    public Flux<User> findAllUsers() {
        return queryMapRows(
                "select u.id,u.username,u.avatar_url,u.tag, " +
                        "p.id as photo_id,p.url,p.time,p.caption,p.owner_id " +
                        "from usr u left join photo p on u.id = p.owner_id",
                connectionFactory)
                .flatMapMany(rows -> {
                    Map<Long, User> users = new HashMap<>();
                    rows.forEach(row -> {
                        User user = userFromRow(row);
                        Photo photo = photoFromRow(row);

                        users.putIfAbsent(user.getId(), user);
                        User user0 = users.get(photo.getOwnerId());
                        if (Objects.isNull(user0.getPhotos())) {
                            user0.setPhotos(new ArrayList<>());
                        }
                        user0.getPhotos().add(photo);
                    });
                    return Flux.fromStream(
                            users.values()
                                    .stream()
                    );
                });
    }

    private Photo photoFromRow(Map<String, Object> row) {
        Photo photo = new Photo();
        photo.setCaption((String) row.get("caption"));
        LocalDate time = (LocalDate) row.get("time");
        photo.setDate(time.atStartOfDay());
        photo.setId((Long) row.get("photo_id"));
        photo.setUrl((String) row.get("url"));
        photo.setOwnerId((Long) row.get("owner_id"));
        return photo;
    }

    private User userFromRow(Map<String, Object> row) {
        User user = new User();
        user.setId((Long) row.get("id"));
        user.setTag((String) row.get("tag"));
        user.setUsername((String) row.get("username"));
        user.setAvatarUrl((String) row.get("avatar_url"));
        return user;
    }
}
