package com.trelloiii.fotogram.configuration;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationManager.class);
    private final JwtUtil jwtUtil;

    public AuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String username=null;
        try {
            username = jwtUtil.extractUsername(token);
        }
        catch (ExpiredJwtException e){
            logger.warn("Expired user token: {}",e.getMessage());
        }
        catch (SignatureException e){
            logger.warn("Invalid user JWT: {}",e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (username!=null && jwtUtil.validate(token)){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtUtil.getRoles(token)
            );
            return Mono.just(authToken);
        }else{
            return Mono.empty();
        }
    }
}
