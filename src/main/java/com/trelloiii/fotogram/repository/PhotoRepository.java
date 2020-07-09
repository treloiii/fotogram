package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.model.PhotoLikes;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PhotoRepository extends ReactiveCrudRepository<PhotoLikes,Long>,PhotoRepositoryExtended {

}
