package com.trelloiii.fotogram.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhotoLikes {
    @Id
    private Long id;
    private Long photoId;
    private Long ownerId;
    private User owner;
}
