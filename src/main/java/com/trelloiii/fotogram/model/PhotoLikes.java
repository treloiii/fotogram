package com.trelloiii.fotogram.model;

import com.trelloiii.fotogram.configuration.IgnoreNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
@IgnoreNull
public class PhotoLikes {
    @Id
    private Long id;
    private Long photoId;
    private Long ownerId;
    private User owner;
}
