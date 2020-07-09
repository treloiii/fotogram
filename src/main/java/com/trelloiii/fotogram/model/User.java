package com.trelloiii.fotogram.model;
import com.trelloiii.fotogram.configuration.IgnoreNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Table("usr")
@IgnoreNull
public class User{
    @Id
    private Long id;
    private String username;
    private String password;
    private String tag;
    @Column("avatar_url")
    private String avatarUrl;

    private List<Photo> photos;
}
