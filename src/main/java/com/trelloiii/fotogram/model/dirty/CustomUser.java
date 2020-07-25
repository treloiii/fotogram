package com.trelloiii.fotogram.model.dirty;

import com.trelloiii.fotogram.model.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUser {
    private long id;
    private String username;
    private String avatarUrl;
    private long subscribersCount;
    private long subscriptionsCount;
}
