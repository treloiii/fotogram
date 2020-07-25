package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.dto.UserProfileDto;
import com.trelloiii.fotogram.model.dirty.CustomUser;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.service.UserService;
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
    public Mono<UserProfileDto> getUserProfileByUsername(@PathVariable String username){
        return userService.getUserProfile(username);
    }

    @GetMapping("/{username}/photos")
    public Flux<Photo> getAllUserPhotos(@PathVariable String username){
        return userService.findAllUserPhotos(username);
    }

    @PostMapping("/photo")
    public Mono<Photo> postPhoto(
            @RequestParam String caption,
            @RequestParam MultipartFile image){
        return null;
    }
}
