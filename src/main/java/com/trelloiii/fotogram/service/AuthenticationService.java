package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.configuration.security.JwtUtil;
import com.trelloiii.fotogram.dto.TokenResponse;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.model.UserRefreshToken;
import com.trelloiii.fotogram.repository.RefreshTokenRepository;
import com.trelloiii.fotogram.repository.UserRepository;
import org.flywaydb.core.internal.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class AuthenticationService {
//    private static final ResponseEntity<String> unauth = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong login or password");
    private final Function<String,ResponseEntity<String>> unauth = (message)->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthenticationService(JwtUtil jwtUtil, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ResponseEntity<?>> authorize(String username, String rawPassword) {
        return userRepository.findUserByUsername(username)
                .flatMap(user -> {
                    String password = user.getPassword();
                    boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, password);
                    if (isPasswordCorrect) {
                        TokenResponse tokenResponse = jwtUtil.createToken(user);
                        refreshTokenRepository.saveByUserId(user.getId(),tokenResponse.getRefreshToken());
                        return Mono.just(ResponseEntity.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(tokenResponse)
                                );
                    }
                    else {
                        return Mono.just(unauth.apply("Wrong username or password"));
                    }
                })
                .defaultIfEmpty(unauth.apply("Wrong username or password"));
    }

    public Mono<ResponseEntity<?>> refreshTokens(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(token->{
                    Long userId = token.getUser_id();
                    return Pair.of(userRepository.findById(userId),token);
                })
                .flatMap(pair->{
                    Mono<User> userMono = pair.getLeft();
                    boolean isRefreshTokenNotExpired = jwtUtil.validate(refreshToken);
                    if(isRefreshTokenNotExpired)
                        return userMono.flatMap(user->{
                                TokenResponse tokenResponse = jwtUtil.createToken(user);
                                UserRefreshToken userRefreshToken = pair.getRight();
                                userRefreshToken.setToken(tokenResponse.getRefreshToken());
                                refreshTokenRepository.saveByUserId(user.getId(),tokenResponse.getRefreshToken());
                                return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(tokenResponse));
                            });
                    else
                        return Mono.just(unauth.apply("Refresh token already expired"));
                })
                .defaultIfEmpty(unauth.apply("Invalid refresh token"));
    }
}
