package com.trelloiii.fotogram.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.views.View;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(View.Important.class)
public class UserProfileDto {
    private User user;
    private Page<Photo> userPhotos;
    private Map<String,Object> additionalInfo;
}
