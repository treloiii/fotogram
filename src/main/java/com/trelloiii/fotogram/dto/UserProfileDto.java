package com.trelloiii.fotogram.dto;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.dirty.CustomUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private CustomUser customUser;
    private List<Photo> userPhotos;
}
