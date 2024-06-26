package org.task.servicegateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.task.servicegateway.exceptions.AuthorizationException;
import org.task.servicegateway.util.JwtUtil;

@Component
public class AuthenticateFilter extends AbstractGatewayFilterFactory<AuthenticateFilter.Config> {
    private final JwtUtil jwtUtil;

    public AuthenticateFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(AuthenticateFilter.Config config) {
        return ((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new AuthorizationException("missing authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            String role;
            try {
                Claims claims = jwtUtil.validateToken(authHeader);
                role = claims.get("role", String.class);
                exchange.getAttributes().put("role", role);
            } catch (Exception e) {
                throw new AuthorizationException("Invalid authentication token");
            }

            if(!role.equals("DOCTOR") && !role.equals("PATIENT")){
                throw new AuthorizationException("Invalid role");
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {}
}
