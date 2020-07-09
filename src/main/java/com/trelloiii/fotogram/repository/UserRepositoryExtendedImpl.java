package com.trelloiii.fotogram.repository;

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
    public Flux<User> findAllUsers() {
        return  queryMapRows(
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
