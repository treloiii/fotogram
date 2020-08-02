package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.configuration.security.JwtUtil;
import com.trelloiii.fotogram.repository.UserRepository;
import com.trelloiii.fotogram.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {
    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //refresh access token and create new refresh token and return both
    @PostMapping("/refresh")
    public Mono<ResponseEntity<?>> refreshToken(@RequestBody Object requestEntity){
        HashMap<String,Object> requestParams = (HashMap<String, Object>) requestEntity;
        String refreshToken = (String)requestParams.get("refresh_token");
        return authenticationService.refreshTokens(refreshToken);
    }

    //returns access token and refresh token to first sign in!!
    @PostMapping
    public Mono<ResponseEntity<?>> authorize(ServerWebExchange serverWebExchange){
        return serverWebExchange.getFormData()
                .flatMap(form -> {
                    String username = form.getFirst("username");
                    String rawPassword = form.getFirst("password");
                    return authenticationService.authorize(username,rawPassword);
                });
    }

}
