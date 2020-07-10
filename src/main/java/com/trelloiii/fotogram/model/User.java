package com.trelloiii.fotogram.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;

@Data
@Table("usr")
@ToString(of={"id"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User{
    @Id
    private Long id;
    private String username;
    private String password;
    private String tag;
    @Column("avatar_url")
    private String avatarUrl;

    private Set<User> subscriber;
    private Set<User> subscriptions;
    private List<Photo> photos;
}
