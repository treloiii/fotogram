package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.extended.PhotoRepositoryExtended;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PhotoRepository extends ReactiveCrudRepository<Photo,Long>, PhotoRepositoryExtended {
//    @Query("select * from photo p where p.owner_id=(select id from usr u where u.username=?username) order by p.id")
//    Flux<Photo> findByOwnerUsername(String username,Pageable pageable);
}
