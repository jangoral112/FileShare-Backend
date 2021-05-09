package com.jango.gateway.filter;

import com.jango.gateway.jwt.JsonWebTokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class AuthorizationFilter implements GatewayFilter {
    
    private final JsonWebTokenProperties jwtProperties;
    private final AuthorizationFilterFactory.Configuration configuration;

    public AuthorizationFilter(JsonWebTokenProperties jwtProperties, AuthorizationFilterFactory.Configuration configuration) {
        this.jwtProperties = jwtProperties;
        this.configuration = configuration;
    }

    @Override 
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        
        String jwt = authHeader.replace("Bearer ", "");
        
        try {
            Claims claims = Jwts.parser()
                                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                                .parseClaimsJws(jwt)
                                .getBody();

            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) claims.get("authorities");
            
            if(authorities.containsAll(Arrays.asList(configuration.getRequiredRoles())) == false) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            
        }catch (ExpiredJwtException e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        
        return chain.filter(exchange);
    }
}
