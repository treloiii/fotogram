package com.trelloiii.fotogram.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.trelloiii.fotogram.dto.UserProfileDto;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.service.UserService;
import com.trelloiii.fotogram.views.View;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService=userService;
    }


    @GetMapping("/{username}")
    @JsonView(View.Full.class)
    public Mono<UserProfileDto> getUserProfileByUsername(@PathVariable String username){
        return userService.getUserProfile(username);
    }

    @PostMapping("/photo")
    public Mono<Photo> postPhoto(
            @RequestParam String caption,
            @RequestParam MultipartFile image){
        return null;
    }
}
