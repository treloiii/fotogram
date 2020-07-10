package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.service.UserService;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService=userService;
    }

    /**
     * For get primary user information
     * @param tag user tag
     * @return User
     * @see com.trelloiii.fotogram.model.User
     */
    @GetMapping("/{tag}")
    public Mono<User> getByTag(@PathVariable String tag){
        return userService.getUserByTag(tag);
    }

    /**
     * For get all user photos
     * @param tag user tag
     * @return array of user photos
     * @see com.trelloiii.fotogram.model.Photo
     */
    @GetMapping("/{tag}/photos")
    public Flux<Photo> getAllUserPhotos(@PathVariable String tag){
        return userService.findAllUserPhotos(tag);
    }
}
