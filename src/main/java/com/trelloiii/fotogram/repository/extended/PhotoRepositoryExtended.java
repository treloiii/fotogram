package com.trelloiii.fotogram.repository.extended;

import com.trelloiii.fotogram.model.Photo;
import reactor.core.publisher.Flux;

public interface PhotoRepositoryExtended {
    Flux<Photo> getAllUserPhotos(String username);
}
