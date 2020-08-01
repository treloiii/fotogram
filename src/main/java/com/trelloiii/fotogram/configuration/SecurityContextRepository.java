package com.trelloiii.fotogram.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager authenticationManager;

    public SecurityContextRepository(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("save() operation is unsupported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        String authHeader = serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        String tokenPrefix = "Bearer ";
        if(authHeader!=null && authHeader.startsWith(tokenPrefix)){
            String token = authHeader.substring(tokenPrefix.length());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    token,
                    token
            );
            return authenticationManager
                    .authenticate(auth)
                    .map(SecurityContextImpl::new);
        }
        return Mono.empty();
    }
}
