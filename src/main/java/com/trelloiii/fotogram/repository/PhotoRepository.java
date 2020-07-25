package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.PhotoLikes;
import com.trelloiii.fotogram.repository.extended.PhotoRepositoryExtended;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface PhotoRepository extends ReactiveCrudRepository<Photo,Long>, PhotoRepositoryExtended {
}
