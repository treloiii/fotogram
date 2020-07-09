package com.trelloiii.fotogram.controllers;

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
    private final ConnectionFactory connectionFactory;
    public UserController(UserService userService, @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        this.userService = userService;
        this.connectionFactory = connectionFactory;
    }

    @GetMapping("/{id}")
    public Mono<User> getById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @GetMapping
    public Flux<User> findAll(){
       return userService.findAllUsers();
    }
}
