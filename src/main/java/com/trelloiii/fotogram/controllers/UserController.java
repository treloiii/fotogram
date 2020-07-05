package com.trelloiii.fotogram.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping
    public Mono<String> hello(){
        return Mono.just("hello world");
    }
}
