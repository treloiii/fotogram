package com.trelloiii.fotogram.repository.extended.implementation;

import com.trelloiii.fotogram.dto.JsonPage;
import com.trelloiii.fotogram.exceptions.UnreachablePageException;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.BaseRepository;
import com.trelloiii.fotogram.repository.extended.PhotoRepositoryExtended;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class PhotoRepositoryExtendedImpl extends BaseRepository implements PhotoRepositoryExtended {
    private final ConnectionFactory connectionFactory;

    public PhotoRepositoryExtendedImpl(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    @Override
    public Mono<Page<Photo>> findByOwnerUsername(String username, Pageable pageable) {
        try {
            long photoTotalRows = getTotalRows("photo", pageable, connectionFactory);
            long offset = pageable.getOffset();
            int pageSize = pageable.getPageSize();
            //Page<Photo> page = new PageImpl<>(List.of(),pageable,photoTotalRows);
            return queryMapRows(
                    "select * from photo p where p.owner_id=" +
                            "(select id from usr u where u.username=?username)" +
                            "order by p.id desc limit ?limit offset ?off;",
                    connectionFactory,
                    Map.of(
                            "username", username,
                            "limit", pageSize,
                            "off", offset
                    )
            )
                    .flatMapMany(rows -> Flux.fromIterable(mapObjects(rows,Photo.class)))
                    .collectList()
                    .flatMap(list-> Mono.just(new JsonPage<>(list,pageable,photoTotalRows)));
        }
        catch (UnreachablePageException e){
            return Mono.just(Page.empty());
        }
    }
}
