package com.trelloiii.fotogram.configuration;

import com.trelloiii.fotogram.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expirationTime;

    public String extractUsername(String token) {
        return getClaimsFromTokenJws(token)
                .getSubject();
    }

    public List<GrantedAuthority> getRoles(String token){
        Claims claims = getClaimsFromTokenJws(token);
        List<String> role = claims.get("role",List.class);
        List<GrantedAuthority> roles = role.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return roles;
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

    public String createToken(User user) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",List.of("ROLE_USER"));

        long expirationSeconds = Long.parseLong(expirationTime);
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime()+expirationSeconds*1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}
