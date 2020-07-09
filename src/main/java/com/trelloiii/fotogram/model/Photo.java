package com.trelloiii.fotogram.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table
@Data
public class Photo{
    @Id
    private Long id;
    private String url;
    private LocalDateTime date;
    private String caption;
    @Column("owner_id")
    private Long ownerId;
}
