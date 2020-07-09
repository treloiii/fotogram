package com.trelloiii.fotogram.model;
import com.trelloiii.fotogram.configuration.IgnoreNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table
@Data
@IgnoreNull
public class Photo{
    @Id
    private Long id;
    private String url;
    private LocalDateTime date;
    private String caption;
    @Column("owner_id")
    private Long ownerId;
    private List<PhotoLikes> likes;
    private Integer likesCount;
}
