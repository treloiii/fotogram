package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface PhotoRepositoryExtended {
    Mono<Page<Photo>> findByOwnerUsername(String username, Pageable pageable);
}
