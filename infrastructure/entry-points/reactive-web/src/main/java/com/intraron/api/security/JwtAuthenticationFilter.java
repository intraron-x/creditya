// Archivo: com/intraron/api/security/JwtAuthenticationFilter.java

package com.intraron.api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null) {
            return userDetailsService.findByUsername(userEmail)
                    .flatMap(userDetails -> {
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            log.info("Roles extraídos del token para el usuario {}: {}", userDetails.getUsername(), userDetails.getAuthorities());
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                        }
                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(chain.filter(exchange));
        }

        return chain.filter(exchange);
    }
}