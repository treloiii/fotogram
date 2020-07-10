package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.Photo;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PhotoRepositoryExtendedImpl extends BaseRepository implements PhotoRepositoryExtended {
    private final ConnectionFactory connectionFactory;

    public PhotoRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Flux<Photo> getAllUserPhotos(String tag) {
        Map<String, Object> binds = new HashMap<>();
        binds.put("tag", tag);
        return queryMapRows("select p.*, pl.owner_id as like_owner from photo p left join photo_likes pl on pl.photo_id = p.id " +
                        "where p.owner_id in " +
                        "(select id from usr u where u.tag = ?tag);",
                connectionFactory,
                binds
        )
                .flatMapMany(rows -> {
                    List<Photo> photoList = new ArrayList<>();
                    rows.forEach(row-> {
                        Photo photo = new Photo();
                        photo.setUrl((String) row.get("url"));
                        photo.setId((Long) row.get("id"));
                        photoList.add(photo);
                    });
                    return Flux.fromIterable(photoList);
                });
    }
}
