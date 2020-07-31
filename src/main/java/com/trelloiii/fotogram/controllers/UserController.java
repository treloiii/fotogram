package com.trelloiii.fotogram.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.trelloiii.fotogram.dto.UserProfileDto;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.service.UserService;
import com.trelloiii.fotogram.views.View;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final static int SUBS_SIZE = 8;
    public UserController(UserService userService) {
        this.userService=userService;
    }


    @GetMapping("/{username}")
    @JsonView(View.Important.class)
    public Mono<UserProfileDto> getUserProfileByUsername(@PathVariable String username){
        return userService.getUserProfile(username);
    }

    @GetMapping("/{username}/subscribers")
    @JsonView(View.Important.class)
    public Mono<Page<User>> getUserSubscribers(
            @PathVariable String username,
            @PageableDefault(size = SUBS_SIZE,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        return userService.getUserSubscribers(username,pageable);
    }

    @GetMapping("/{username}/subscriptions")
    @JsonView(View.Important.class)
    public Mono<Page<User>> getUserSubscriptions(
            @PathVariable String username,
            @PageableDefault(size = SUBS_SIZE,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        return userService.getUserSubscriptions(username,pageable);
    }

}
