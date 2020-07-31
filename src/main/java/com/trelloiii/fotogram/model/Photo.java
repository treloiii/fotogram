package com.trelloiii.fotogram.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.trelloiii.fotogram.views.View;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Photo{
    @Id
    @JsonView(View.Important.class)
    private Long id;
    @JsonView(View.Important.class)
    private String url;
    @JsonView(View.Full.class)
    private LocalDate time;
    @JsonView(View.Full.class)
    private String caption;
    @JsonIgnore
    private Long ownerId;
    @JsonView(View.Full.class)
    @Transient
    private User owner;
    @Transient
    @JsonView(View.Full.class)
    private List<PhotoLikes> likes;
}
