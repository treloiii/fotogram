package com.trelloiii.fotogram.configuration.security;

import com.trelloiii.fotogram.dto.TokenResponse;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.model.UserRefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.exp}")
    private String expirationAccess;
    @Value("${jwt.refresh.exp}")
    private String expirationRefresh;

    public String extractUsername(String token) {
        return getClaimsFromTokenJws(token)
                .getSubject();
    }

    public List<GrantedAuthority> getRoles(String token){
        Claims claims = getClaimsFromTokenJws(token);
        List<String> role = claims.get("role",List.class);
        try {
            return role.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        catch (NullPointerException e){
            logger.warn("Null roles list from user JWT");
            return null;
        }
    }
    private Claims getClaimsFromTokenJws(String token) {
        String key = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        return getClaimsFromTokenJws(token)
                .getExpiration()
                .after(new Date());
    }

    public TokenResponse createToken(User user) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",List.of("ROLE_USER"));

        long expirationSeconds = Long.parseLong(expirationAccess);
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime()+expirationSeconds*1000);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        String refreshToken = createRefreshToken(user);
        return new TokenResponse(
                accessToken,
                refreshToken,
                expirationSeconds
        );
    }

    private String createRefreshToken(User user){
        long expirationSeconds = Long.parseLong(expirationRefresh);
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime()+expirationSeconds*1000);

        return Jwts.builder()
//                .setClaims(Map.of("role","REFRESH_TOKEN"))
                .setSubject("refresh_for_"+user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}
