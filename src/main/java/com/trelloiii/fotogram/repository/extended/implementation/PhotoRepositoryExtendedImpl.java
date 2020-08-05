package com.trelloiii.fotogram.repository.extended.implementation;

import com.trelloiii.fotogram.exceptions.UnreachablePageException;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.BaseRepository;
import com.trelloiii.fotogram.repository.extended.PhotoRepositoryExtended;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class PhotoRepositoryExtendedImpl extends BaseRepository implements PhotoRepositoryExtended {
    public Mono<EntityContainer<Photo>> findFullPhotoById(Long id) {
        return queryMapRow(
                "select p.*,u.username,u.avatar_url\n" +
                        "    ,(select count(*) from photo_likes pl where pl.photo_id = p.id) as likes_count\n" +
                        "    ,(select count(*) from photo_comments pc where pc.photo_id = p.id) as comments_count\n" +
                        "    from photo p\n" +
                        "    join usr u on p.owner_id = u.id\n" +
                        "    where p.id = :id",
                Map.of("id", id)
        )
                .flatMap(row -> {
                    EntityContainer<Photo> photoContainer = mapObject(row, Photo.class);
                    Photo photo = photoContainer.getEntity();
                    photo.setOwner(
                            new User(
                                    (long) row.get("owner_id"),
                                    (String) row.get("username"),
                                    null,//no password!
                                    (String) row.get("avatar_url")
                            )
                    );
                    return Mono.just(new EntityContainer<>(
                            photo,
                            Map.of(
                                    "likes_count", row.get("likes_count"),
                                    "comments_count", row.get("comments_count")
                            )
                    ));
                });
    }

    @Override
    public Mono<Page<Photo>> findByOwnerUsername(String username, Pageable pageable) {
        try {
            long offset = pageable.getOffset();
            int pageSize = pageable.getPageSize();
            return queryMapRows(
                    "select *, count(*) over() as count from photo p where p.owner_id=" +
                            "(select id from usr u where u.username = :username ) " +
                            "order by p.id desc limit :limit offset :off;",
                    Map.of(
                            "username", username,
                            "limit", pageSize,
                            "off", offset
                    )
            )
                    .flatMap(rows -> pagedEntity(mapObjects(rows, Photo.class), pageable));
        } catch (UnreachablePageException e) {
            return Mono.just(Page.empty());
        }
    }
}
