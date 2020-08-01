package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.configuration.JwtUtil;
import com.trelloiii.fotogram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final ResponseEntity<?> unauth = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong login or password");
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginController(@Qualifier("passwordEncoder") PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping
    public Mono<ResponseEntity<?>> authorize(ServerWebExchange serverWebExchange){
        return serverWebExchange.getFormData()
                .flatMap(form -> {
                    String username = form.getFirst("username");
                    String rawPassword = form.getFirst("password");
                    return userRepository.findUserByUsername(username)
                            .flatMap(user -> {
                                String password = user.getPassword();
                                boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, password);
                                if (isPasswordCorrect)
                                    return Mono.just(ResponseEntity.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(jwtUtil.createToken(user)));
                                else {
                                    return Mono.just(unauth);
                                }
                            })
                            .defaultIfEmpty(unauth);
                });
    }

}
